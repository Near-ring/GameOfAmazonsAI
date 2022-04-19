package amazon_ai.remote_cpp_agent;

import amazon_framework.AbstractAIAgent;
import amazon_framework.OrderedPair;
import amazon_framework.OrderedPairList;
import static amazon_framework.Operations.*;

public class RemoteCPPAgent extends AbstractAIAgent {
    private JavaClient client = new JavaClient();
    int n = 0;

    public RemoteCPPAgent(byte[][] board, byte side) {
        super(board, side);
        byte[] nweo = new byte[7];
        nweo[6] = side;
        client.cppInterface(nweo);
        newState = board;
    }

    public void write_action_remote(OrderedPairList vms){
        byte[] action = new byte[7];
        if (vms.isEmpty()){
            action[6] = 3;
            client.cppInterface(action);
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return;
        }
        action[0] = (byte)vms.at(0).x;
        action[1] = (byte)vms.at(0).y;
        action[2] = (byte)vms.at(1).x;
        action[3] = (byte)vms.at(1).y;
        action[4] = (byte)vms.at(2).x;
        action[5] = (byte)vms.at(2).y;
        action[6] = 0;
        client.cppInterface(action);
    }

    public OrderedPairList receiveAction(){
        byte[] req = new byte[7];
        // 4 for req action
        req[6] = 4;
        byte[] action;
        action = client.cppInterface(req);
        OrderedPairList ret = new OrderedPairList();
        if (action[6] == 3){
            return ret;
        }
        ret.add(new OrderedPair(action[0], action[1]));
        ret.add(new OrderedPair(action[2], action[3]));
        ret.add(new OrderedPair(action[4], action[5]));
        return ret;
    }

    @Override
    public void calculate() {
        if (n>0 || side == BLACK){
            write_action_remote(parseAction(newState, gameBoard));
        }
        n++;
        OrderedPairList lastaction = receiveAction();
        // copy last state
        newState = matrixCopy(gameBoard);
        forceAction(newState, lastaction.at(0),lastaction.at(1), MOVE);
		forceAction(newState, lastaction.at(1),lastaction.at(2), BLOCK);
    }
}
