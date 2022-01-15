package airport;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class PossiblePath {
    private final String thisCity;
    private final boolean isDestiny;
    private final List<PossiblePath> connections;


    public PossiblePath(boolean isDest, String from) {
        thisCity = from;

        this.isDestiny = isDest;
        connections = new LinkedList<>();
        //if (isDest)
        //    isDestiny = true;
        //else{
        //    isDestiny = false;
        //    connections = new LinkedList<>();
        //}
    }

    public void addPossiblePath(PossiblePath toInsert){
        connections.add(toInsert);
    }

    public int numPossiblePaths(){
        return connections.size();
    }

    @Override
    public String toString() {
        if (isDestiny) return " [Destination " + thisCity + "] ";
        StringBuilder res = new StringBuilder( "airport.PossiblePath{ here: " + thisCity );
        for (PossiblePath connection : connections){
            res.append("\n  {" + connection.toString() + "}");
        }
        return res.toString();
    }

    public String toStringPretty(String start){
        if (isDestiny) return start + " [" + thisCity + "]\n ";

        String header = start + " " + thisCity;
        StringBuilder res = new StringBuilder();

        for (PossiblePath connection : connections){
             res.append(connection.toStringPretty(header));
        }
        return res.toString();
    }

    //public static PossiblePath deserialize(byte[] bytes) {
    //    System.out.println("Entrei aqui");
    //    System.out.println(bytes.length);
    //    ByteBuffer bb = ByteBuffer.wrap(bytes);

    //    byte[] thisCityBytes = new byte[bb.getInt()];
    //    bb.get(thisCityBytes);
    //    String thisCity = new String(thisCityBytes, StandardCharsets.UTF_8);
    //    int size = bb.getInt();
    //    PossiblePath possiblePath = new PossiblePath(size == 0, thisCity);

    //    List<PossiblePath> list = new ArrayList<>();
    //    for (int i = 0; i < size; i++ ) {
    //        System.out.println("recebi");
    //        list.add(deserialize(bytes));
    //    }

    //    for (PossiblePath path : list)
    //        possiblePath.addPossiblePath(path);
    //    //logger.info("deserialize route: " + possiblePath);
    //    return possiblePath;
    //}

    //public byte[] serialize() {
    //    ByteBuffer bb = ByteBuffer.allocate( Integer.BYTES + thisCity.length() + Integer.BYTES);

    //    bb.putInt(thisCity.length());
    //    bb.put(thisCity.getBytes(StandardCharsets.UTF_8));

    //    bb.putInt(connections.size());

    //    for (PossiblePath possiblePath : connections)  {
    //        System.out.println("Hummm");
    //        byte[] elem = possiblePath.serialize();
    //        ByteBuffer newBuffer = ByteBuffer.allocate(bb.capacity() + elem.length);
    //        newBuffer.put(bb); newBuffer.put(elem);
    //        bb = newBuffer;
    //    }
    //    //System.out.println(bb);
    //    return bb.array();
    //}
}
