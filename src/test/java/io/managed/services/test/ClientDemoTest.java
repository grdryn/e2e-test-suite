package io.managed.services.test;

import io.fabric8.kubernetes.client.Config;
import io.fabric8.kubernetes.client.ConfigBuilder;
import io.fabric8.openshift.client.DefaultOpenShiftClient;
import io.fabric8.openshift.client.OpenShiftClient;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import io.fabric8.kubernetes.client.KubernetesClientException;

@Log4j2
public class ClientDemoTest extends TestBase {

    private OpenShiftClient oc;

    @BeforeClass
    @SneakyThrows
    public void bootstrap() {
        log.info("Bootstrap");

        log.info("build config");
        Config config = new ConfigBuilder()
                .withMasterUrl("https://api.mk-stage-0622.bd59.p1.openshiftapps.com:6443")
                .withOauthToken(Environment.PROMETHEUS_WEB_CLIENT_ACCESS_TOKEN)
                .withTrustCerts(true)
                .build();

        log.info("init openshift client");
        oc = new DefaultOpenShiftClient(config);
    }

    // oc client for stage cluster
    @Test
    public void getNamespacesSuccess() throws Exception {
        log.info("perform operation that is allowed for given token: get namespaces ");

        var output = oc.namespaces().list();
        output
                .getItems()
                .stream()
                .filter(ns -> ns.getMetadata().getName().equals("prom-query-ns"))
                .findAny()
                .orElseThrow( () ->new Exception("not found"));
        log.info("namespace found");
    }

    @Test
    public void getPodFailed() throws Exception {
        log.info("fail to perform operation forbidden for given token: get pods ");
        Assert.assertThrows( KubernetesClientException.class, ()->  oc.pods().list());
    }


}
