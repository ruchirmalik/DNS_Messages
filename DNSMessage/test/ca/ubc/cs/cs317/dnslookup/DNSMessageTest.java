package ca.ubc.cs.cs317.dnslookup;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class DNSMessageTest {
    @Test
    public void testConstructor() {
        DNSMessage message = new DNSMessage((short)23);
        assertFalse(message.getQR());
        assertFalse(message.getRD());
        assertEquals(0, message.getQDCount());
        assertEquals(0, message.getANCount());
        assertEquals(0, message.getNSCount());
        assertEquals(0, message.getARCount());
        assertEquals(23, message.getID());
    }
    @Test
    public void testBasicFieldAccess() {
        DNSMessage message = new DNSMessage((short)23);
        message.setID(46);
        message.setQR(true);
        message.setOpcode(15);
        message.setAA(true);
        message.setTC(true);
        message.setRD(true);
        message.setRA(true);
        message.setRcode(14);
        message.setQDCount(1);
        message.setARCount(100);

        assertEquals(46, message.getID());
        assertTrue(message.getQR());
        assertEquals(15, message.getOpcode());
        assertTrue(message.getAA());
        assertTrue(message.getTC());
        assertTrue(message.getRD());
        assertTrue(message.getRA());
        assertEquals(14, message.getRcode());
        assertEquals(1, message.getQDCount());
        assertEquals(100, message.getARCount());

        message.setRA(true);
        System.out.println("RA: " +message.getRA());
        message.setRA(false);
        System.out.println("RA: " +message.getRA());

    }



    @Test
    public void testAddQuestion() {
        DNSMessage request = new DNSMessage((short)23);
        DNSQuestion question = new DNSQuestion("norm.cs.ubc.ca", RecordType.A, RecordClass.IN);
        request.addQuestion(question);
        byte[] content = request.getUsed();

        DNSMessage reply = new DNSMessage(content, content.length);
        assertEquals(request.getID(), reply.getID());
        assertEquals(request.getQDCount(), reply.getQDCount());
        assertEquals(request.getANCount(), reply.getANCount());
        assertEquals(request.getNSCount(), reply.getNSCount());
        assertEquals(request.getARCount(), reply.getARCount());
        DNSQuestion replyQuestion = reply.getQuestion();
        assertEquals(question, replyQuestion);
    }
    @Test
    public void testAddResourceRecord() {
        DNSMessage request = new DNSMessage((short)23);
        DNSQuestion question = new DNSQuestion("norm.cs.ubc.ca", RecordType.A, RecordClass.IN);
        ResourceRecord rr = new ResourceRecord(question, RecordType.A.getCode(), "ns1.cs.ubc.ca");
        request.addResourceRecord(rr);
        byte[] content = request.getUsed();

        DNSMessage reply = new DNSMessage(content, content.length);
        assertEquals(request.getID(), reply.getID());
        assertEquals(request.getQDCount(), reply.getQDCount());
        assertEquals(request.getANCount(), reply.getANCount());
        assertEquals(request.getNSCount(), reply.getNSCount());
        assertEquals(request.getARCount(), reply.getARCount());
        ResourceRecord replyRR = reply.getRR();
        assertEquals(rr, replyRR);
    }
}
