
package com.telecom.oms.order.service;

import com.telecom.oms.order.dto.CreateOrderRequest;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

@Component
public class OrderRequestHasher {

    public String hash(CreateOrderRequest request) {
        StringBuilder canonicalRequest = new StringBuilder();

        canonicalRequest.append(request.accountNumber()).append("|");

        request.items().forEach(item ->
                canonicalRequest
                        .append(item.productId())
                        .append(":")
                        .append(item.quantity())
                        .append("|")
        );

        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            byte[] hashBytes = digest.digest(
                    canonicalRequest.toString().getBytes(StandardCharsets.UTF_8)
            );

            return HexFormat.of().formatHex(hashBytes);

        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException(
                    "SHA-256 algorithm is not available",
                    exception
            );
        }
    }
}
