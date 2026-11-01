package com.codequest.engine;

import org.springframework.stereotype.Service;

@Service
public class CodeWrapperService {

    public String wrapPython(String userCode) {
        StringBuilder sb = new StringBuilder();
        sb.append("import sys\n");
        sb.append("import json\n");
        sb.append("import inspect\n");
        sb.append("\n");
        sb.append("_actions = []\n");
        sb.append("_max_actions = 500\n");
        sb.append("\n");
        sb.append("def _get_line():\n");
        sb.append("    try:\n");
        sb.append("        frame = inspect.currentframe().f_back.f_back\n");
        sb.append("        return max(1, frame.f_lineno - 47)\n");
        sb.append("    except Exception:\n");
        sb.append("        return 1\n");
        sb.append("\n");
        sb.append("def move():\n");
        sb.append("    if len(_actions) >= _max_actions:\n");
        sb.append("        raise RuntimeError('Too many actions - possible infinite loop')\n");
        sb.append("    _actions.append({'type': 'move', 'line': _get_line()})\n");
        sb.append("\n");
        sb.append("def turn_left():\n");
        sb.append("    if len(_actions) >= _max_actions:\n");
        sb.append("        raise RuntimeError('Too many actions - possible infinite loop')\n");
        sb.append("    _actions.append({'type': 'turn_left', 'line': _get_line()})\n");
        sb.append("\n");
        sb.append("def turn_right():\n");
        sb.append("    if len(_actions) >= _max_actions:\n");
        sb.append("        raise RuntimeError('Too many actions - possible infinite loop')\n");
        sb.append("    _actions.append({'type': 'turn_right', 'line': _get_line()})\n");
        sb.append("\n");
        sb.append("def collect():\n");
        sb.append("    if len(_actions) >= _max_actions:\n");
        sb.append("        raise RuntimeError('Too many actions - possible infinite loop')\n");
        sb.append("    _actions.append({'type': 'collect', 'line': _get_line()})\n");
        sb.append("\n");
        sb.append("def attack():\n");
        sb.append("    if len(_actions) >= _max_actions:\n");
        sb.append("        raise RuntimeError('Too many actions - possible infinite loop')\n");
        sb.append("    _actions.append({'type': 'attack', 'line': _get_line()})\n");
        sb.append("\n");
        sb.append("def wall_ahead():\n");
        sb.append("    return False\n");
        sb.append("\n");
        sb.append("def gem_here():\n");
        sb.append("    return False\n");
        sb.append("\n");
        sb.append("# === USER CODE ===\n");
        sb.append("try:\n");

        for (String line : userCode.split("\n")) {
            sb.append("    ").append(line).append("\n");
        }

        sb.append("except Exception as e:\n");
        sb.append("    _actions.append({'type': 'error', 'details': str(e)})\n");
        sb.append("\n");
        sb.append("print('__ACTIONS__')\n");
        sb.append("print(json.dumps(_actions))\n");

        return sb.toString();
    }

    public String wrapJava(String userCode) {
        StringBuilder sb = new StringBuilder();
        sb.append("import java.util.ArrayList;\n");
        sb.append("import java.util.List;\n");
        sb.append("\n");
        sb.append("public class Solution {\n");
        sb.append("    private static List<String> _actions = new ArrayList<>();\n");
        sb.append("    private static int _maxActions = 500;\n");
        sb.append("\n");
        sb.append("    public static void move() {\n");
        sb.append("        if (_actions.size() >= _maxActions) throw new RuntimeException(\"Too many actions\");\n");
        sb.append("        _actions.add(\"{\\\"type\\\":\\\"move\\\"}\");\n");
        sb.append("    }\n");
        sb.append("\n");
        sb.append("    public static void turnLeft() { turn_left(); }\n");
        sb.append("    public static void turn_left() {\n");
        sb.append("        if (_actions.size() >= _maxActions) throw new RuntimeException(\"Too many actions\");\n");
        sb.append("        _actions.add(\"{\\\"type\\\":\\\"turn_left\\\"}\");\n");
        sb.append("    }\n");
        sb.append("\n");
        sb.append("    public static void turnRight() { turn_right(); }\n");
        sb.append("    public static void turn_right() {\n");
        sb.append("        if (_actions.size() >= _maxActions) throw new RuntimeException(\"Too many actions\");\n");
        sb.append("        _actions.add(\"{\\\"type\\\":\\\"turn_right\\\"}\");\n");
        sb.append("    }\n");
        sb.append("\n");
        sb.append("    public static void collect() {\n");
        sb.append("        if (_actions.size() >= _maxActions) throw new RuntimeException(\"Too many actions\");\n");
        sb.append("        _actions.add(\"{\\\"type\\\":\\\"collect\\\"}\");\n");
        sb.append("    }\n");
        sb.append("\n");
        sb.append("    public static void attack() {\n");
        sb.append("        if (_actions.size() >= _maxActions) throw new RuntimeException(\"Too many actions\");\n");
        sb.append("        _actions.add(\"{\\\"type\\\":\\\"attack\\\"}\");\n");
        sb.append("    }\n");
        sb.append("\n");
        sb.append("    public static boolean wallAhead() { return wall_ahead(); }\n");
        sb.append("    public static boolean wall_ahead() { return false; }\n");
        sb.append("\n");
        sb.append("    public static boolean gemHere() { return gem_here(); }\n");
        sb.append("    public static boolean gem_here() { return false; }\n");
        sb.append("\n");
        sb.append("    public static void main(String[] args) {\n");
        sb.append("        try {\n");

        for (String line : userCode.split("\n")) {
            sb.append("            ").append(line).append("\n");
        }

        sb.append("        } catch (Exception e) {\n");
        sb.append("            _actions.add(\"{\\\"type\\\":\\\"error\\\",\\\"details\\\":\\\"\" + e.getMessage().replace(\"\\\"\", \"'\") + \"\\\"}\");\n");
        sb.append("        }\n");
        sb.append("        System.out.println(\"__ACTIONS__\");\n");
        sb.append("        System.out.print(\"[\");\n");
        sb.append("        for (int i = 0; i < _actions.size(); i++) {\n");
        sb.append("            System.out.print(_actions.get(i));\n");
        sb.append("            if (i < _actions.size() - 1) System.out.print(\",\");\n");
        sb.append("        }\n");
        sb.append("        System.out.println(\"]\");\n");
        sb.append("    }\n");
        sb.append("}\n");

        return sb.toString();
    }

}
