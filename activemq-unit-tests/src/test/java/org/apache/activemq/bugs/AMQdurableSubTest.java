/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.activemq.bugs;

import org.apache.activemq.EmbeddedBrokerAndConnectionTestSupport;
import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.broker.jmx.BrokerViewMBean;
import org.junit.Test;

import javax.jms.*;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

/**
 * @author <a href="http://www.christianposta.com/blog">Christian Posta</a>
 */
public class AMQdurableSubTest extends EmbeddedBrokerAndConnectionTestSupport {

    private static final String CLIENT_ID = "amq-test-client-id";
    private static final String DURABLE_SUB_NAME = "testDurable";

    @Override
    protected void setUp() throws Exception {
        this.useTopic = true;
        super.setUp();
    }

    @Test
    public void testFoo() throws Exception {
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        MessageConsumer consumer = session.createDurableSubscriber((Topic)createDestination(), "testDurable");
        consumer.close();


        BrokerViewMBean brokerView = getBrokerView(DURABLE_SUB_NAME);
        brokerView.destroyDurableSubscriber(CLIENT_ID, DURABLE_SUB_NAME);
    }

    private BrokerViewMBean getBrokerView(String testDurable) throws MalformedObjectNameException {
        ObjectName brokerName = new ObjectName("org.apache.activemq:type=Broker,brokerName=localhost");
        BrokerViewMBean view = (BrokerViewMBean)broker.getManagementContext().newProxyInstance(brokerName, BrokerViewMBean.class, true);
        assertNotNull(view);
        return view;
    }


    @Override
    protected Connection createConnection() throws Exception {
        Connection rc = super.createConnection();
        rc.setClientID(CLIENT_ID);
        return rc;
    }

    @Override
    protected BrokerService createBroker() throws Exception {
        BrokerService rc = super.createBroker();
        rc.setOfflineDurableSubscriberTaskSchedule(172800000);
        rc.setOfflineDurableSubscriberTimeout(3600000);
        rc.setSchedulerSupport(true);
        return rc;
    }
}
