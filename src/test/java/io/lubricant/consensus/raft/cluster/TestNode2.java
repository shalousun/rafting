package io.lubricant.consensus.raft.cluster;

import io.lubricant.consensus.raft.RaftContainer;
import io.lubricant.consensus.raft.cluster.cmd.AppendCommand;
import io.lubricant.consensus.raft.cluster.cmd.FileBasedTestFactory;
import io.lubricant.consensus.raft.command.RaftStub;
import io.lubricant.consensus.raft.support.RaftException;
import io.lubricant.consensus.raft.support.anomaly.NotLeaderException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadLocalRandom;

public class TestNode2 {

    static {
        System.setProperty("LOG_FILE_NAME", "raft2");
    }

    private static final Logger logger = LoggerFactory.getLogger(TestNode2.class);

    public static void main(String[] args) throws Exception {
        RaftContainer container = new RaftContainer("raft2.xml");
        container.create(new FileBasedTestFactory());
        RaftStub root;
        while (true) try {
            root = container.getStub("root");
            if (root != null) break;
            container.openContext("root", true);
            break;
        } catch (RaftException e) {
            logger.warn("create failed", e);
            Thread.sleep(5000);
        }
        logger.info("create root context done");
        while (true) {
            try {
                int rand = ThreadLocalRandom.current().nextInt(1000);
                root.submit(new AppendCommand("node2-" + rand));
//                Boolean result = root.execute(new AppendCommand("node2-" + rand), 1000);
//                logger.info("execute result: {}", result);
            } catch (Throwable ex) {
                if (ex instanceof ExecutionException) {
                    ex = ex.getCause();
                }
                if (! (ex instanceof NotLeaderException)) {
                    logger.info("execute failed: {}", ex.getClass().getSimpleName());
                }
            }
            Thread.sleep(10);
        }
    }

}
