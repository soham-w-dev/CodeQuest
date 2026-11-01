import java.util.ArrayList;
import java.util.List;

public class Solution {
    private static List<String> _actions = new ArrayList<>();
    private static int _maxActions = 500;

    public static void move() {
        if (_actions.size() >= _maxActions) throw new RuntimeException("Too many actions");
        _actions.add("{\"type\":\"move\"}");
    }

    public static void turnLeft() { turn_left(); }
    public static void turn_left() {
        if (_actions.size() >= _maxActions) throw new RuntimeException("Too many actions");
        _actions.add("{\"type\":\"turn_left\"}");
    }

    public static void turnRight() { turn_right(); }
    public static void turn_right() {
        if (_actions.size() >= _maxActions) throw new RuntimeException("Too many actions");
        _actions.add("{\"type\":\"turn_right\"}");
    }

    public static void collect() {
        if (_actions.size() >= _maxActions) throw new RuntimeException("Too many actions");
        _actions.add("{\"type\":\"collect\"}");
    }

    public static void attack() {
        if (_actions.size() >= _maxActions) throw new RuntimeException("Too many actions");
        _actions.add("{\"type\":\"attack\"}");
    }

    public static boolean wallAhead() { return wall_ahead(); }
    public static boolean wall_ahead() { return false; }

    public static boolean gemHere() { return gem_here(); }
    public static boolean gem_here() { return false; }

    public static void main(String[] args) {
        try {
            // Navigate around the wall obstacle!
            // Available methods: move(); turnRight(); turnLeft();
            
            for(int i=0;i<2;i++)
                move();
            
            turnRight();
            
            for(int i=0;i<5;i++)
                move();
            
            turnLeft();
            
            for(int i=0;i<3;i++)
                move
        } catch (Exception e) {
            _actions.add("{\"type\":\"error\",\"details\":\"" + e.getMessage().replace("\"", "'") + "\"}");
        }
        System.out.println("__ACTIONS__");
        System.out.print("[");
        for (int i = 0; i < _actions.size(); i++) {
            System.out.print(_actions.get(i));
            if (i < _actions.size() - 1) System.out.print(",");
        }
        System.out.println("]");
    }
}
