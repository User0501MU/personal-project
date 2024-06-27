package com.techacademy; // ご自身の環境に合わせてください

import org.apache.catalina.connector.Connector;
import org.apache.coyote.ajp.AjpNio2Protocol;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TomcatConfiguration implements WebServerFactoryCustomizer<TomcatServletWebServerFactory> {

    @Override
    public void customize(TomcatServletWebServerFactory factory) {
        // Tomcatとの通信手段にAJPを追加
        factory.addAdditionalTomcatConnectors(ajpConnector());
    }

    private Connector ajpConnector() {
        // AJPの利用を宣言
        Connector connector = new Connector("org.apache.coyote.ajp.AjpNio2Protocol");

        // AJPの通信で8009番ポートを使うための指定
        connector.setPort(8009);

        // secretという保護機能を利用しないようにする設定
        AjpNio2Protocol protocol = (AjpNio2Protocol) connector.getProtocolHandler();
        protocol.setSecretRequired(false);

        return connector;
    }

}