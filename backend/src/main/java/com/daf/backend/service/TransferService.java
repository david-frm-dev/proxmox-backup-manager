package com.daf.backend.service;

import com.daf.backend.dto.ManifestDto;
import com.daf.backend.model.BackupRecord;
import com.daf.backend.model.BackupTarget;
import com.daf.backend.model.ProxmoxConnection;
import com.daf.backend.repository.ProxmoxConnectionRepository;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

@Slf4j
@Service
@AllArgsConstructor
public class TransferService {
    private final ProxmoxConnectionRepository connectionRepository;

    /**
     * Downloads the VZDUMP File Proxmox-Machine
     *
     * @param remotePath need the destination of the file to download
     * @return a inputStream to download the file
     * @throws SftpException when JSch can't put or has JSch has other problems with connecting to target destination
     *
     */
    public InputStream downloadFromProxmox(String remotePath) throws Exception {
        ProxmoxConnection connection = connectionRepository.findFirstBy().orElseThrow();
        String host = connection.getSshIpAddress();
        String user = connection.getSshUser();
        int port = connection.getSshPort();
        String password = new String(connection.getSshKeyEnc());

        log.info("Download proxmox from {}:{}:{}", host, user, port);

        JSch jsch = new JSch();
        Session session = jsch.getSession(user, host, port);
        session.setPassword(password);
        session.setConfig("StrictHostKeyChecking", "no");
        session.connect(10_000);

        ChannelSftp sftp = (ChannelSftp) session.openChannel("sftp");
        sftp.connect();

        return sftp.get(remotePath);
    }

    /**
     * Uploads the backuped file to destination server.
     *
     * @param target    is used to connect to specific backup destination
     * @param localFile is used to upload the correct file
     * @param fileName  is used to write the filename on destination server
     * @return remotePath as String
     * @throws SftpException when JSch can't put or has JSch has other problems with connecting to target destination
     *
     */
    public String uploadToTarget(BackupTarget target, Path localFile, String fileName, String node, int vmid) throws Exception {
        JSch jsch = new JSch();
        Session session = jsch.getSession(target.getUsername(), target.getHost(), target.getPort());
        session.setPassword(target.getCredentialsEnc());
        session.setConfig("StrictHostKeyChecking", "no");
        session.connect(10_000);

        ChannelSftp sftp = (ChannelSftp) session.openChannel("sftp");
        sftp.connect();

        String remoteDir = target.getBasePath() + "/" + node + "/" + vmid;
        ensureDirectory(sftp, remoteDir);

        String remotePath = remoteDir + "/" + fileName;

        try (InputStream in = Files.newInputStream(localFile)) {
            sftp.put(in, remotePath);
        } finally {
            sftp.disconnect();
            session.disconnect();
        }

        return remotePath;
    }

    public void writeManifest(BackupTarget target, BackupRecord record) throws Exception {
        ManifestDto manifest = new ManifestDto(
                record.getVmid(),
                record.getFilename(),
                record.getSizeBytes(),
                record.getSha256Orig(),
                record.getSha256Enc(),
                record.getEncrypted(),
                record.getStartedAt()
        );

        ObjectMapper mapper = new ObjectMapper();
        byte[] valueAsBytes = mapper.writeValueAsBytes(manifest);

        JSch jsch = new JSch();
        Session session = jsch.getSession(target.getUsername(), target.getHost(), target.getPort());
        session.setPassword(target.getCredentialsEnc());
        session.setConfig("StrictHostKeyChecking", "no");
        session.connect(10_000);

        ChannelSftp sftp = (ChannelSftp) session.openChannel("sftp");
        sftp.connect();

        String remoteDir = target.getBasePath() + "/" + record.getNode() + "/" + record.getVmid();
        ensureDirectory(sftp, remoteDir);

        String remotePath = remoteDir + "/" + record.getFilename() + ".manifest.json";

        try (ByteArrayInputStream bais = new ByteArrayInputStream(valueAsBytes)) {
            sftp.put(bais, remotePath);
        } finally {
            sftp.disconnect();
            session.disconnect();
        }
    }


    public void deleteFromTarget(BackupTarget target, String remotePath) throws Exception {
        JSch jsch = new JSch();
        Session session = jsch.getSession(target.getUsername(), target.getHost(), target.getPort());
        session.setPassword(target.getCredentialsEnc());
        session.setConfig("StrictHostKeyChecking", "no");
        session.connect(10_000);

        ChannelSftp sftp = (ChannelSftp) session.openChannel("sftp");
        sftp.connect();

        try {
            sftp.rm(remotePath);
        } finally {
            sftp.disconnect();
            session.disconnect();
        }
    }

    public InputStream downloadFromTarget(BackupTarget target, String remotePath) throws Exception {
        JSch jsch = new JSch();
        Session session = jsch.getSession(target.getUsername(), target.getHost(), target.getPort());
        session.setPassword(target.getCredentialsEnc());
        session.setConfig("StrictHostKeyChecking", "no");
        session.connect(10_000);

        ChannelSftp sftp = (ChannelSftp) session.openChannel("sftp");
        sftp.connect();

        return sftp.get(remotePath);
    }

    private void ensureDirectory(ChannelSftp sftp, String directoryPath) throws SftpException {
        String[] path = directoryPath.split("/");
        String currentPath = "";

        for (String p : path) {
            if (p.isEmpty()) continue;
            currentPath += "/" + p;

            try {
                sftp.stat(currentPath);
            } catch (SftpException _) {
                sftp.mkdir(currentPath);
            }
        }
    }
}
