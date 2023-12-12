//package com.hongwen.network;
//
//import java.io.BufferedReader;
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.IOException;
//import java.io.InputStreamReader;
//import java.math.BigInteger;
//import java.security.KeyStore;
//import java.security.KeyStoreException;
//import java.security.MessageDigest;
//import java.security.NoSuchAlgorithmException;
//import java.security.PublicKey;
//import java.security.cert.Certificate;
//import java.security.cert.CertificateEncodingException;
//import java.security.cert.CertificateException;
//import java.security.interfaces.RSAPublicKey;
//
//public class APKSignatureGenerator {
//    public static void main(String[] args) {
//        try {
//            // 提示用户输入相关信息
//            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
//            System.out.print("请输入公司名称：");
//            String companyName = reader.readLine();
//
//            System.out.print("请输入密钥库密码：");
//            String keystorePassword = reader.readLine();
//
//            System.out.print("请输入别名：");
//            String alias = reader.readLine();
//
//            System.out.print("请输入别名密码：");
//            String aliasPassword = reader.readLine();
//
//            // 生成 APK 签名文件的命令
//            String[] cmd = {
//                    "keytool",
//                    "-genkeypair",
//                    "-keystore",
//                    "keystore.jks",
//                    "-alias",
//                    alias,
//                    "-keyalg",
//                    "RSA",
//                    "-validity",
//                    "10950",
//                    "-keysize",
//                    "2048"
//            };
//
//            // 执行命令生成 APK 签名文件
//            ProcessBuilder pb = new ProcessBuilder(cmd);
//            pb.directory(new File("."));
//            Process process = pb.start();
//            process.waitFor();
//
//            // 读取生成的 APK 签名文件
//            FileInputStream fis = new FileInputStream("keystore.jks");
//
//            String storeFilePath = new File(new File("."), "keystore.jks").getAbsolutePath();
//
//            // 加载密钥库文件
//            KeyStore keyStore = KeyStore.getInstance("JKS");
//            keyStore.load(fis, keystorePassword.toCharArray());
//            fis.close();
//
//
//            // 获取证书和公钥
//            Certificate cert = keyStore.getCertificate(alias);
//            PublicKey publicKey = cert.getPublicKey();
//
//            // 输出公钥信息
//            String publicKeyType = publicKey.getAlgorithm();
//            System.out.println("公钥类型: " + publicKeyType);
//
//            if ("RSA".equals(publicKeyType)) {
//                BigInteger exponent = ((RSAPublicKey) publicKey).getPublicExponent();
//                System.out.println("指数: " + exponent);
//
//                BigInteger modulus = ((RSAPublicKey) publicKey).getModulus();
//                System.out.println("模数: " + modulus);
//
//                // 计算并输出指定算法的摘要信息
//                getDigestPair(cert, "MD5");
//                getDigestPair(cert, "SHA1");
//                getDigestPair(cert, "SHA-256");
//
//                System.out.println("密钥库文件路径: " + storeFilePath);
//                System.out.println("密钥库密码: " + keystorePassword);
//                System.out.println("别名密码: " + aliasPassword);
//                System.out.println("别名: " + alias);
//            }
//
//            fis.close();
//        } catch (IOException | NoSuchAlgorithmException | CertificateException | KeyStoreException |
//                 InterruptedException e) {
//            e.printStackTrace();
//        }
//    }
//
//    public static void getDigestPair(Certificate cert, String algorithm) throws CertificateEncodingException, NoSuchAlgorithmException {
//        // 计算指定算法的摘要信息
//        MessageDigest md = MessageDigest.getInstance(algorithm);
//        byte[] digest = md.digest(cert.getEncoded());
//
//        System.out.println("算法: " + algorithm);
//        System.out.println("摘要: " + byteArrayToHexString(digest));
//    }
//
//    public static String byteArrayToHexString(byte[] bytes) {
//        StringBuilder sb = new StringBuilder();
//        for (byte b : bytes) {
//            sb.append(String.format("%02X", b));
//        }
//        return sb.toString();
//    }
//}
//
//
