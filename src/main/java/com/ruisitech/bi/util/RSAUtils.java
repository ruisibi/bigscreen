/*
 * Copyright 2018 本系统版权归成都睿思商智科技有限公司所有 
 * 用户不能删除系统源码上的版权信息, 使用许可证地址:
 * https://www.ruisitech.com/licenses/opensource.html
 */
package com.ruisitech.bi.util;

import org.apache.commons.codec.binary.Base64;

import javax.crypto.Cipher;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.Security;
import java.security.spec.PKCS8EncodedKeySpec;

/**
 * 非对称加密/解密实现
 * @author hq
 *
 */
public class RSAUtils {

    private static final String pubKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCTg/rOum0AdvQ5h6CBTKs1hNKCBvy2BT6Dk06OQbLX33aoaNPdhBibYMtVmpc5qTs2mBpGUa/h9dlWvSUxkBPNazpg4odKcnrPfSQ3T3t/wee8WWkVQixsrXOYUiIu8UgEajvopSL73RfvN9CxBFKSZzwBuCbu2Zr8tEPsA+WREwIDAQAB";
    private static final String privKey = "MIICdQIBADANBgkqhkiG9w0BAQEFAASCAl8wggJbAgEAAoGBAJOD+s66bQB29DmHoIFMqzWE0oIG/LYFPoOTTo5Bstffdqho092EGJtgy1WalzmpOzaYGkZRr+H12Va9JTGQE81rOmDih0pyes99JDdPe3/B57xZaRVCLGytc5hSIi7xSARqO+ilIvvdF+830LEEUpJnPAG4Ju7Zmvy0Q+wD5ZETAgMBAAECgYACtAt3mGEA/i0RlNOzKuWEUI/PBDPDXis8HAn3f2q4nAa/pVQknhQZXNe9FN0qmFhWcskgesS/XlMCTOsOknZaaQhlcLzKYxBeSFkFJzJ+PEaEP9K9jMnq/rH7j8Y2YPP1v2t3TkyV0Gx4+uVPjfr6VfWYpKgEh8xSsRvkWw9ToQJBAPVp7nkoN1Qk+jSyoFesiThLvjg/FTrSVLxcCkpj9D/EYO8l4kqWzLYV2IUA1RQl7IdLHlhbTkeO9bdqZY17QdECQQCZ4PjiZQQ6hiVG4xhOo2MVR3ifJCpVevcyv64OL5hyWRI9ptDm08fFG8tgQjpN2XMoNxnjYoBsWYLzxdDdOVmjAkB0qjYezJp42tdDBAOrii71XzR/YqXB/8kefha6RIgRZFa8eWL5GHUavoneuIWS/XBS5Mf5Mvakl/aBigfa2VRxAkAsfdzYcMbBOSosDdtoWVt7upeIxActgglZdgyYc3CLl29rvgDmzNMVDc1G/r8m5TmjphLpKQDi0xrG6ypR3JQrAkBnZm2F58B9Lc+mUn1UDVwcB/HP/Ui6YkUVSAg42tGE/1NrAMh3ZOZdo1QyftchqMLuGhAMPYGo6A/rtQL5g+B5";

    public static String generateBase64PublicKey() {
        return pubKey;
    }
    public static String decryptBase64(String string) {
        return new String(decrypt(Base64.decodeBase64(string.getBytes())));
    }
    private static byte[] decrypt(byte[] byteArray) {
        try {
            Provider provider = new org.bouncycastle.jce.provider.BouncyCastleProvider();
            Security.addProvider(provider);
            Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding", provider);

            byte[] keyBytes = java.util.Base64.getDecoder().decode(privKey.getBytes());
            PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(keyBytes);
            KeyFactory keyf = KeyFactory.getInstance("RSA");
            PrivateKey privateKey = keyf.generatePrivate(pkcs8KeySpec);
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            byte[] plainText = cipher.doFinal(byteArray);
            return plainText;
        } catch(Exception e) {
            throw new RuntimeException(e);
        }
    }
}
