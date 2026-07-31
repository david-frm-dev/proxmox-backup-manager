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

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Date;
import java.sql.Timestamp;

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
    public String uploadToTarget(BackupTarget target, Path localFile, String fileName) throws Exception {
        JSch jsch = new JSch();
        Session session = jsch.getSession(target.getUsername(), target.getHost(), target.getPort());
        session.setPassword(target.getCredentialsEnc());
        session.setConfig("StrictHostKeyChecking", "no");
        session.connect(10_000);

        ChannelSftp sftp = (ChannelSftp) session.openChannel("sftp");
        sftp.connect();

        String remotePath = target.getBasePath() + "/" + fileName;

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




    }
}
