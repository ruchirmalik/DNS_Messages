package ca.ubc.cs.cs317.dnslookup;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.IntStream;

public class DNSMessage {
    public static final int MAX_DNS_MESSAGE_LENGTH = 512;
    /**
     * TODO:  You will add additional constants and fields
     */
    private int ID;
    private int QR;
    private int AA;
    private int OP;
    private int TC;
    private int RD;
    private int RA;
    private int RCode;
    private int QDCount;
    private int ANCount;
    private int NSCount;
    private int ARCount;

    private final Map<String, Integer> nameToPosition = new HashMap<>();
    private final Map<Integer, String> positionToName = new HashMap<>();
    private final ByteBuffer buffer;


    /**
     * Initializes an empty DNSMessage with the given id.
     *
     * @param id The id of the message.
     */
    public DNSMessage(short id) {
        this.buffer = ByteBuffer.allocate(MAX_DNS_MESSAGE_LENGTH);
        // TODO: Complete this method
        buffer.position(0);
        buffer.putShort(id);
        buffer.position(12);
    }

    /**
     * Initializes a DNSMessage with the first length bytes of the given byte array.
     *
     * @param recvd The byte array containing the received message
     * @param length The length of the data in the array
     */
    public DNSMessage(byte[] recvd, int length) {
        buffer = ByteBuffer.wrap(recvd, 0, length);
        // TODO: Complete this method

        buffer.position(12);
    }

    /**
     * Getters and setters for the various fixed size and fixed location fields of a DNSMessage
     * TODO:  They are all to be completed
     */
    public int getID() {
        ID = buffer.getShort(0) & 0xffff;
        return ID;
    }

    public void setID(int id) {
        buffer.putShort(0, (short)id);
    }

    public boolean getQR()
    {
        QR = buffer.get(2) & 0x80;
        if(QR == 0)
            return false; //it is a query
        return true; //it is a response

    }

    public void setQR(boolean qr) {
        buffer.put(2, (byte)(buffer.get(2) & 0x7f));
        if(qr)
            buffer.put(2, (byte)(buffer.get(2) | 0x80)); //setting QR to 1 because it's a response
        buffer.put(2, (byte)(buffer.get(2) | 0x00));
    }

    public boolean getAA()
    {
       AA = buffer.get(2) & 0x04;
       if(AA == 0)
           return false;
       return true;
    }

    public void setAA(boolean aa)
    {
        buffer.put(2, (byte)(buffer.get(2) & 0xfb)); //setting AA to 0 first
        if(aa)
            buffer.put(2, (byte)(buffer.get(2) | 0x04)); //setting AA to 1 because it's a response
        buffer.put(2, (byte)(buffer.get(2) | 0x00));
    }

    public int getOpcode()
    {
        OP = (buffer.get(2) & 0x78) >> 3;
        return OP;
    }

    //sure about this
    public void setOpcode(int opcode)
    {
        //String op = Integer.toBinaryString(opcode);
        //String xx = Integer.toBinaryString(15);
        //System.out.println("15: " +xx);
        //System.out.println("OPCODE: " +op);
        buffer.put(2, (byte)(buffer.get(2) & 0x87));
        opcode = opcode << 3;
//        if(op.length() == 1)
//            op = "0000" + op + "000";
//        else if(op.length() == 2)
//            op = "000" + op + "000";
//        else if(op.length() == 3)
//            op = "00" + op + "000";
//        else
//            op = "0" + op + "000";
        //System.out.println(op);
        //int converted = Integer.parseInt(op);
        //System.out.println(converted);
        //System.out.println(buffer.get(2));
        buffer.put(2, (byte)((buffer.get(2) | opcode)));
    }

    public boolean getTC()
    {
       TC = (buffer.get(2) & 0x02);
       if(TC == 0)
           return false;
       return true;
    }

    public void setTC(boolean tc)
    {
        buffer.put(2, (byte)(buffer.get(2) & 0xfd));
        if(tc)
            buffer.put(2, (byte)(buffer.get(2) | 0x02)); //setting TC to 1 because it's a response
        buffer.put(2, (byte)(buffer.get(2) | 0x00));
    }

    public boolean getRD()
    {
        RD = (buffer.get(2) & 0x01);
        if(RD == 0)
            return false;
        return true;
    }

    public void setRD(boolean rd)
    {
        buffer.put(2, (byte)(buffer.get(2) & 0xfe));
        if(rd)
            buffer.put(2, (byte)(buffer.get(2) | 0x01)); //setting rd to 1 because it needs recursion.
        buffer.put(2, (byte)(buffer.get(2) | 0x00));
    }

    public boolean getRA()
    {
        RA = (buffer.get(3) & 0x80);
        if(RA == 0)
            return false;
        return true;
    }

    public void setRA(boolean ra)
    {
        buffer.put(3, (byte)(buffer.get(3) & 0x7f));
        if(ra)
            buffer.put(3, (byte)(buffer.get(3) | 0x80));
        buffer.put(3, (byte)(buffer.get(3) | 0x00));
    }

    public int getRcode()
    {
        RCode = (buffer.get(3) & 0x0f);
        return RCode;
    }

    //unsure about thissss
    public void setRcode(int rcode)
    {
        //String rc = Integer.toBinaryString(rcode);
        buffer.put(3, (byte)(buffer.get(3) & 0xf0)); //setting RCODE to 0
        buffer.put(3, (byte)(buffer.get(3) | rcode));
    }

    public int getQDCount()
    {
        QDCount = buffer.getShort(4) & 0xffff;
        return QDCount;
    }

    public void setQDCount(int count)
    {
        buffer.putShort(4, (short)count);
    }

    public int getANCount()
    {
        ANCount = buffer.getShort(6) & 0xffff;
        return ANCount;
    }

    public int getNSCount()
    {
        NSCount = buffer.getShort(8) & 0xffff;
        return NSCount;
    }

    public int getARCount()
    {
        ARCount = buffer.getShort(10) & 0xffff;
        return ARCount;
    }

    public void setARCount(int count)
    {
        buffer.putShort(10, (short)count);
    }

    /**
     * Return the name at the current position() of the buffer.  This method is provided for you,
     * but you should ensure that you understand what it does and how it does it.
     *
     * The trick is to keep track of all the positions in the message that contain names, since
     * they can be the target of a pointer.  We do this by storing the mapping of position to
     * name in the positionToName map.
     *
     * @return The decoded name
     */
    public String getName() {
        // Remember the starting position for updating the name cache
        int start = buffer.position();
        int len = buffer.get() & 0xff;
        if (len == 0) return "";
        if ((len & 0xc0) == 0xc0) {  // This is a pointer
            int pointer = ((len & 0x3f) << 8) | (buffer.get() & 0xff);
            String suffix = positionToName.get(pointer);
            assert suffix != null;
            positionToName.put(start, suffix);
            return suffix;
        }
        byte[] bytes = new byte[len];
        buffer.get(bytes, 0, len);
        String label = new String(bytes, StandardCharsets.UTF_8);
        String suffix = getName();
        String answer = suffix.isEmpty() ? label : label + "." + suffix;
        positionToName.put(start, answer);
        return answer;
    }

    /**
     * The standard toString method that displays everything in a message.
     * @return The string representation of the message
     */
    public String toString() {
        // Remember the current position of the buffer so we can put it back
        // Since toString() can be called by the debugger, we want to be careful to not change
        // the position in the buffer.  We remember what it was and put it back when we are done.
        int end = buffer.position();
        final int DataOffset = 12;
        try {
            StringBuilder sb = new StringBuilder();
            sb.append("ID: ").append(getID()).append(' ');
            sb.append("QR: ").append(getQR()).append(' ');
            sb.append("OP: ").append(getOpcode()).append(' ');
            sb.append("AA: ").append(getAA()).append('\n');
            sb.append("TC: ").append(getTC()).append(' ');
            sb.append("RD: ").append(getRD()).append(' ');
            sb.append("RA: ").append(getRA()).append(' ');
            sb.append("RCODE: ").append(getRcode()).append(' ')
                    .append(dnsErrorMessage(getRcode())).append('\n');
            sb.append("QDCount: ").append(getQDCount()).append(' ');
            sb.append("ANCount: ").append(getANCount()).append(' ');
            sb.append("NSCount: ").append(getNSCount()).append(' ');
            sb.append("ARCount: ").append(getARCount()).append('\n');
            buffer.position(DataOffset);
            showQuestions(getQDCount(), sb);
            showRRs("Authoritative", getANCount(), sb);
            showRRs("Name servers", getNSCount(), sb);
            showRRs("Additional", getARCount(), sb);
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "toString failed on DNSMessage";
        }
        finally {
            buffer.position(end);
        }
    }

    /**
     * Add the text representation of all the questions (there are nq of them) to the StringBuilder sb.
     *
     * @param nq Number of questions
     * @param sb Collects the string representations
     */
    private void showQuestions(int nq, StringBuilder sb) {
        sb.append("Question [").append(nq).append("]\n");
        for (int i = 0; i < nq; i++) {
            DNSQuestion question = getQuestion();
            sb.append('[').append(i).append(']').append(' ').append(question).append('\n');
        }
    }

    /**
     * Add the text representation of all the resource records (there are nrrs of them) to the StringBuilder sb.
     *
     * @param kind Label used to kind of resource record (which section are we looking at)
     * @param nrrs Number of resource records
     * @param sb Collects the string representations
     */
    private void showRRs(String kind, int nrrs, StringBuilder sb) {
        sb.append(kind).append(" [").append(nrrs).append("]\n");
        for (int i = 0; i < nrrs; i++) {
            ResourceRecord rr = getRR();
            sb.append('[').append(i).append(']').append(' ').append(rr).append('\n');
        }
    }

    /**
     * Decode and return the question that appears next in the message. The current position in the
     * buffer indicates where the question starts.
     *
     * @return The decoded question
     */
    public DNSQuestion getQuestion() {
        // TODO: Complete this method
        String qname = getName();
        RecordType qtype = RecordType.getByCode(buffer.getShort());
        RecordClass qclass = RecordClass.getByCode(buffer.getShort());

        DNSQuestion question = new DNSQuestion(qname, qtype, qclass);
        return question;

    }

    /**
     * Decode and return the resource record that appears next in the message.  The current
     * position in the buffer indicates where the resource record starts.
     *
     * @return The decoded resource record
     */
    public ResourceRecord getRR()
    {
        // TODO: Complete this method

        DNSQuestion question = getQuestion();
        int ttl = buffer.getInt();
        int rlen = buffer.getShort();

        //type A - 32 bits
        if(question.getRecordType() == RecordType.A) {
            int address = buffer.getInt();

            //converting an integer address to a byte array because getByAddress requires a byte array as parameter
            ByteBuffer b = ByteBuffer.allocate(4);
            b.putInt(address);
            byte[] add = b.array();

            try {
                InetAddress result = InetAddress.getByAddress(add);
                ResourceRecord rr = new ResourceRecord(question, ttl, result);
                return rr;
            }
            catch (UnknownHostException e)
            {
            }
        }
        //type AAAA - 128 bits
        else if(question.getRecordType() == RecordType.AAAA)
        {
            int address1 = buffer.getInt();
            int address2 = buffer.getInt();
            int address3 = buffer.getInt();
            int address4 = buffer.getInt();


            //converting an integer address to a byte array because getByAddress requires a byte array as parameter
            ByteBuffer b = ByteBuffer.allocate(16);
            b.putInt(address1);
            b.putInt(address2);
            b.putInt(address3);
            b.putInt(address4);

            byte[] add = b.array();

            try {
                InetAddress result = InetAddress.getByAddress(add); //requires byte[] b as its parameter
                ResourceRecord rr = new ResourceRecord(question, ttl, result);
                return rr;
            }
            catch (UnknownHostException e)
            {
                System.out.println("Error: " +e);
            }

        }

        //type MX
        else if(question.getRecordType() == RecordType.MX)
        {
            short preference = buffer.getShort();
            String exchange = getName();
            ResourceRecord rr = new ResourceRecord(question, ttl, exchange);
            return rr;
        }

        //type NS or type CNAME
        else
        {
            String name = getName();
            ResourceRecord rr = new ResourceRecord(question, ttl, name);
            return rr;
        }

        return null;
    }

    /**
     * Helper function that returns a hex string representation of a byte array. May be used to represent the result of
     * records that are returned by a server but are not supported by the application (e.g., SOA records).
     *
     * @param data a byte array containing the record data.
     * @return A string containing the hex value of every byte in the data.
     */
    private static String byteArrayToHexString(byte[] data) {
        return IntStream.range(0, data.length).mapToObj(i -> String.format("%02x", data[i])).reduce("", String::concat);
    }

    /**
     * Add an encoded name to the message. It is added at the current position and uses compression
     * as much as possible.  Compression is accomplished by remembering the position of every added
     * label.
     *
     * @param name The name to be added
     */
    public void addName(String name) {
        String label;
        while (name.length() > 0) {
            Integer offset = nameToPosition.get(name);
            if (offset != null) {
                int pointer = offset;
                pointer |= 0xc000;
                buffer.putShort((short)pointer);
                return;
            } else {
                nameToPosition.put(name, buffer.position());
                int dot = name.indexOf('.');
                label = (dot > 0) ? name.substring(0, dot) : name;
                buffer.put((byte)label.length());
                for (int j = 0; j < label.length(); j++) {
                    buffer.put((byte)label.charAt(j));
                }
                name = (dot > 0) ? name.substring(dot + 1) : "";
            }
        }
        buffer.put((byte)0);
    }

    /**
     * Add an encoded question to the message at the current position.
     * @param question The question to be added
     */
    public void addQuestion(DNSQuestion question) {
        // TODO: Complete this method
        addName(question.getHostName());
        addQType(question.getRecordType());
        addQClass(question.getRecordClass());
        //System.out.println(getQDCount());
        setQDCount(getQDCount() + 1); //FUCCCKKKKKKKKKK incrementing the number of questions
        //System.out.println(getQDCount());
    }

    /**
     * Add an encoded resource record to the message at the current position.
     * @param rr The resource record to be added
     */
    public void addResourceRecord(ResourceRecord rr) {
        // TODO: Complete this method

        //addQuestion(rr.getQuestion());
        //setQDCount(getQDCount() - 1); //we subtract 1 because QDCount shouldn't increment but AddQuestion increments it
        addName(rr.getHostName());
        addQType(rr.getRecordType());
        addQClass(rr.getRecordClass());

        buffer.putInt((int)rr.getRemainingTTL()); //ttl

        //type A
        if(rr.getRecordType() == RecordType.A) {
            buffer.putShort((short) 4); //rdlength
            try {
                InetAddress res = InetAddress.getByName(rr.getTextResult());
                int value = ByteBuffer.wrap(res.getAddress()).getInt(); //STACKOVERFLOW
                //System.out.println("InetAddress: " +res);
                //String adr = res.getHostAddress();
                //System.out.println("Address: " +adr);
                //String s = adr.toString();
                //System.out.println("Address in String: " +s);
                //System.out.println("InetAddress in Integer: " +Integer.parseInt(add));

                buffer.putInt(value);
            } catch (UnknownHostException e) {
                System.out.println("ERROR");
            }
        }
        //type AAAA
        else if(rr.getRecordType() == RecordType.AAAA)
        {
            buffer.putShort((short) 16); //rdlength
            try {
                InetAddress res = InetAddress.getByName(rr.getTextResult());
                ByteBuffer bb = ByteBuffer.wrap(res.getAddress());
                int value1 = bb.getInt();
                int value2 = bb.getInt();
                int value3 = bb.getInt();
                int value4 = bb.getInt();//STACKOVERFLOW

                buffer.putInt(value1);
                buffer.putInt(value2);
                buffer.putInt(value3);
                buffer.putInt(value4);
            }
            catch(Exception e)
            {}
        }
        //type MX
        else if (rr.getRecordType() == RecordType.MX)
        {
            buffer.putShort((short) 0); //rdlength
            buffer.putShort((short)0); //preference - 16 bits
            addName(rr.getTextResult());
        }
        //type CNAME or NS
        else
        {
            buffer.putShort((short) 0); //rdlength
            addName(rr.getTextResult());
        }

        setARCount(getARCount() + 1);
    }

    /**
     * Add an encoded type to the message at the current position.
     * @param recordType The type to be added
     */
    private void addQType(RecordType recordType) {
        // TODO: Complete this method
        // if(recordType == RecordType.A)
        //     buffer.putShort((short)1);
        // else if(recordType == RecordType.NS)
        //     buffer.putShort((short)2);
        // else if(recordType == RecordType.CNAME)
        //     buffer.putShort((short)5);
        // else if(recordType == RecordType.SOA)
        //     buffer.putShort((short)6);
        // else if(recordType == RecordType.MX)
        //     buffer.putShort((short)15);
        // else if(recordType == RecordType.AAAA)
        //     buffer.putShort((short)28);
        // else
        //     buffer.putShort((short)0);

            buffer.putShort((short)recordType.getCode());

    }

    /**
     * Add an encoded class to the message at the current position.
     * @param recordClass The class to be added
     */
    private void addQClass(RecordClass recordClass) {
        // TODO: Complete this method
        // if(recordClass == RecordClass.IN)
        //     buffer.putShort((short)1);
        // else
        //     buffer.putShort((short)0);

            buffer.putShort((short)recordClass.getCode());
    }

    /**
     * Return a byte array that contains all the data comprising this message.  The length of the
     * array will be exactly the same as the current position in the buffer.
     * @return A byte array containing this message's data
     */
    public byte[] getUsed() {
        // TODO: Complete this method
        return buffer.array();
    }

    /**
     * Returns a string representation of a DNS error code.
     *
     * @param error The error code received from the server.
     * @return A string representation of the error code.
     */
    public static String dnsErrorMessage(int error) {
        final String[] errors = new String[]{
                "No error", // 0
                "Format error", // 1
                "Server failure", // 2
                "Name error (name does not exist)", // 3
                "Not implemented (parameters not supported)", // 4
                "Refused" // 5
        };
        if (error >= 0 && error < errors.length)
            return errors[error];
        return "Invalid error message";
    }
}

