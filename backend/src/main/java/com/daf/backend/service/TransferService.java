package com.daf.backend.service;

import com.daf.backend.model.ProxmoxConnection;
import com.daf.backend.repository.ProxmoxConnectionRepository;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.InputStream;

@Service
@AllArgsConstructor
public class TransferService {
    private final ProxmoxConnectionRepository connectionRepository;

    public InputStream downloadFromProxmox(String remotePath) throws Exception {
        ProxmoxConnection connection = connectionRepository.findFirstBy().orElseThrow();
        String host = extractHost(connection.getBaseUrl()); // z.B. "192.168.1.250" aus "https://192.168.1.250:8006"
        String user = connection.getSshUser();
        String password = new String(connection.getSshKeyEnc());

        JSch jsch = new JSch();
        Session session = jsch.getSession(user, host, 22);
        session.setPassword(password);
        session.setConfig("StrictHostKeyChecking", "no");
        session.connect(10_000);

        ChannelSftp sftp = (ChannelSftp) session.openChannel("sftp");
        sftp.connect();

        return sftp.get(remotePath);
    }

    private String extractHost(String baseUrl) {
        // "https://192.168.1.250:8006" → "192.168.1.250"
        return baseUrl.replace("https://", "").replace("http://", "").split(":")[0];
    }
}
