package com.codequest.seeder;

import com.codequest.entity.Achievement;
import com.codequest.entity.Course;
import com.codequest.entity.Level;
import com.codequest.entity.World;
import com.codequest.repository.AchievementRepository;
import com.codequest.repository.CourseRepository;
import com.codequest.repository.LevelRepository;
import com.codequest.repository.WorldRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final CourseRepository courseRepository;
    private final WorldRepository worldRepository;
    private final LevelRepository levelRepository;
    private final AchievementRepository achievementRepository;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        seedAchievements();

        // --- Python Course & World ---
        Course pythonCourse = courseRepository.findByLanguage("PYTHON")
                .orElseGet(() -> courseRepository.save(Course.builder()
                        .language("PYTHON")
                        .name("Python Adventure")
                        .description("Learn Python through fun game challenges.")
                        .icon("🐍")
                        .build()));

        World pythonWorld = worldRepository.findByCourseIdOrderByOrderIndexAsc(pythonCourse.getId()).stream()
                .findFirst()
                .orElseGet(() -> worldRepository.save(World.builder()
                        .course(pythonCourse)
                        .name("Code Forest")
                        .description("Your first steps into the coding wilderness. Master basic commands to control your robot through the forest.")
                        .theme("forest")
                        .orderIndex(1)
                        .unlockStarsRequired(0)
                        .build()));

        createOrUpdatePythonLevels(pythonWorld);

        // --- Java Course & World ---
        Course javaCourse = courseRepository.findByLanguage("JAVA")
                .orElseGet(() -> courseRepository.save(Course.builder()
                        .language("JAVA")
                        .name("Java Quest")
                        .description("Master Java by solving exciting puzzles.")
                        .icon("☕")
                        .build()));

        World javaWorld = worldRepository.findByCourseIdOrderByOrderIndexAsc(javaCourse.getId()).stream()
                .findFirst()
                .orElseGet(() -> worldRepository.save(World.builder()
                        .course(javaCourse)
                        .name("Code Forest")
                        .description("Your first steps into the coding wilderness. Master basic commands to control your robot through the forest.")
                        .theme("forest")
                        .orderIndex(1)
                        .unlockStarsRequired(0)
                        .build()));

        createOrUpdateJavaLevels(javaWorld);
    }

    private void seedAchievements() {
        if (achievementRepository.count() > 0) return;

        achievementRepository.save(Achievement.builder()
                .name("First Steps")
                .description("Complete your very first mission!")
                .icon("🌟")
                .category("PROGRESS")
                .xpReward(50)
                .coinReward(20)
                .build());

        achievementRepository.save(Achievement.builder()
                .name("Loop Master")
                .description("Master repeated actions by completing 3 levels.")
                .icon("🔁")
                .category("SKILL")
                .xpReward(100)
                .coinReward(50)
                .build());

        achievementRepository.save(Achievement.builder()
                .name("Code Warrior")
                .description("Conquer an entire world by completing 5 levels!")
                .icon("⚔️")
                .category("WORLD")
                .xpReward(200)
                .coinReward(100)
                .build());

        achievementRepository.save(Achievement.builder()
                .name("Star Collector")
                .description("Earn 10 or more stars across all levels.")
                .icon("⭐")
                .category("STARS")
                .xpReward(150)
                .coinReward(75)
                .build());
    }

    private void upsertLevel(World world, Level newLevel) {
        Optional<Level> existing = levelRepository.findByWorldIdAndOrderIndex(world.getId(), newLevel.getOrderIndex());
        if (existing.isPresent()) {
            Level level = existing.get();
            level.setTitle(newLevel.getTitle());
            level.setDescription(newLevel.getDescription());
            level.setDifficulty(newLevel.getDifficulty());
            level.setConcept(newLevel.getConcept());
            level.setMissionText(newLevel.getMissionText());
            level.setLevelConfig(newLevel.getLevelConfig());
            level.setStarterCode(newLevel.getStarterCode());
            level.setSolutionCode(newLevel.getSolutionCode());
            level.setXpReward(newLevel.getXpReward());
            level.setCoinReward(newLevel.getCoinReward());
            level.setHints(newLevel.getHints());
            levelRepository.save(level);
        } else {
            newLevel.setWorld(world);
            levelRepository.save(newLevel);
        }
    }

    private void createOrUpdatePythonLevels(World world) {
        // Level 1 - First Steps (Sequencing)
        upsertLevel(world, Level.builder()
                .world(world)
                .title("First Steps")
                .description("Learn basic command sequencing")
                .difficulty(Level.Difficulty.EASY)
                .concept("Sequencing (Command Execution)")
                .missionText("WELCOME EXPLORER! Your robotic rover is ready for its first mission in CodeForest. The goal portal is 3 steps ahead. Write move() commands to step forward and reach the portal!")
                .levelConfig("{\"rows\":5,\"cols\":5,\"playerStart\":{\"row\":2,\"col\":0},\"playerDirection\":\"E\",\"goal\":{\"row\":2,\"col\":3},\"walls\":[],\"gems\":[]}")
                .starterCode("# Move the robot forward to the portal!\n# Each move() command advances 1 tile forward.\n\n# TODO: Write move() commands to step forward and reach the portal\n")
                .solutionCode("# Step 1: Advance 3 tiles forward to reach the goal portal\nmove()\nmove()\nmove()")
                .xpReward(50)
                .coinReward(10)
                .orderIndex(1)
                .hints("[{\"text\":\"The robot starts on column 0. The goal portal is at column 3.\",\"cost\":0},{\"text\":\"Use the move() command to step forward.\",\"cost\":5},{\"text\":\"Write move() three times — one per line.\",\"cost\":10}]")
                .build());

        // Level 2 - The Long Bridge (For Loops)
        upsertLevel(world, Level.builder()
                .world(world)
                .title("The Long Bridge")
                .description("Discover how for-loops repeat commands")
                .difficulty(Level.Difficulty.EASY)
                .concept("For Loops (Repetition)")
                .missionText("A long bridge stretches across a deep canyon — 6 tiles long! Instead of writing move() six separate times, write a Python for-loop to repeat the action efficiently.")
                .levelConfig("{\"rows\":3,\"cols\":8,\"playerStart\":{\"row\":1,\"col\":0},\"playerDirection\":\"E\",\"goal\":{\"row\":1,\"col\":6},\"walls\":[{\"row\":0,\"col\":0},{\"row\":0,\"col\":1},{\"row\":0,\"col\":2},{\"row\":0,\"col\":3},{\"row\":0,\"col\":4},{\"row\":0,\"col\":5},{\"row\":0,\"col\":6},{\"row\":0,\"col\":7},{\"row\":2,\"col\":0},{\"row\":2,\"col\":1},{\"row\":2,\"col\":2},{\"row\":2,\"col\":3},{\"row\":2,\"col\":4},{\"row\":2,\"col\":5},{\"row\":2,\"col\":6},{\"row\":2,\"col\":7}],\"gems\":[]}")
                .starterCode("# Cross the 6-tile bridge using a for loop!\n# Use Python's for-loop and range() to repeat move() across all 6 tiles.\n\n# TODO: Write your for-loop below\n\n")
                .solutionCode("# Step 1: Use a for-loop to repeat the forward move 6 times across the bridge\nfor i in range(6):\n    move()")
                .xpReward(75)
                .coinReward(15)
                .orderIndex(2)
                .hints("[{\"text\":\"The bridge is a straight path 6 steps long.\",\"cost\":0},{\"text\":\"In Python, write: for i in range(6): followed by an indented move().\",\"cost\":5},{\"text\":\"for i in range(6):\\n    move()\",\"cost\":10}]")
                .build());

        // Level 3 - Variable Supply Run (Variables & Arithmetic)
        upsertLevel(world, Level.builder()
                .world(world)
                .title("Variable Supply Run")
                .description("Store step distances inside Python variables")
                .difficulty(Level.Difficulty.EASY)
                .concept("Variables & Assignment")
                .missionText("Welcome to the Variable Terminal! Store the distance in a variable named steps = 4, then pass steps into a for-loop to drive the rover to the supply depot.")
                .levelConfig("{\"rows\":3,\"cols\":6,\"playerStart\":{\"row\":1,\"col\":0},\"playerDirection\":\"E\",\"goal\":{\"row\":1,\"col\":4},\"walls\":[],\"gems\":[]}")
                .starterCode("# Create a variable 'steps' storing the distance (4)\n# Then use a for-loop to move that many times.\n\n# TODO: Declare the variable 'steps'\n\n# TODO: Loop using 'steps' to reach the depot\n")
                .solutionCode("# Step 1: Store the distance in a variable\nsteps = 4\n\n# Step 2: Loop through 'steps' distance to reach the supply depot\nfor i in range(steps):\n    move()")
                .xpReward(80)
                .coinReward(20)
                .orderIndex(3)
                .hints("[{\"text\":\"Assign steps = 4 on line 2.\",\"cost\":0},{\"text\":\"Write for i in range(steps): on line 4.\",\"cost\":5},{\"text\":\"steps = 4\\nfor i in range(steps):\\n    move()\",\"cost\":10}]")
                .build());

        // Level 4 - The Maze (Direction Control)
        upsertLevel(world, Level.builder()
                .world(world)
                .title("The Maze")
                .description("Navigate around dangerous wall obstacles")
                .difficulty(Level.Difficulty.MEDIUM)
                .concept("Direction Control & Obstacle Avoidance")
                .missionText("A heavy stone wall blocks your direct path! Use turn_right() and turn_left() to change the robot's heading, navigate around the wall, and reach the exit at (5,5).")
                .levelConfig("{\"rows\":6,\"cols\":6,\"playerStart\":{\"row\":0,\"col\":0},\"playerDirection\":\"E\",\"goal\":{\"row\":5,\"col\":5},\"walls\":[{\"row\":0,\"col\":3},{\"row\":1,\"col\":3},{\"row\":2,\"col\":3},{\"row\":3,\"col\":3}],\"gems\":[]}")
                .starterCode("# Navigate around the wall obstacle to reach the goal at (5,5)!\n# Available commands: move(), turn_right(), turn_left()\n\n# TODO: Steer around the wall and reach the portal\n")
                .solutionCode("# Step 1: Advance 2 steps East to approach the obstacle\nmove()\nmove()\n\n# Step 2: Turn South to bypass the stone wall (rows 0 to 3)\nturn_right()\nfor i in range(4):\n    move()\n\n# Step 3: Turn East and move 3 steps past the wall barrier\nturn_left()\nfor i in range(3):\n    move()\n\n# Step 4: Turn South and step into the goal portal at (5, 5)\nturn_right()\nmove()")
                .xpReward(100)
                .coinReward(20)
                .orderIndex(4)
                .hints("[{\"text\":\"You cannot walk through walls. Go East 2 steps, then turn right to go South.\",\"cost\":0},{\"text\":\"Move down past row 3, turn left, move East 3 steps, then turn right to the goal at (5,5).\",\"cost\":10},{\"text\":\"move() x2 -> turn_right() -> move() x4 -> turn_left() -> move() x3 -> turn_right() -> move()\",\"cost\":15}]")
                .build());

        // Level 5 - The Forking Paths (If/Else Conditionals)
        upsertLevel(world, Level.builder()
                .world(world)
                .title("The Forking Paths")
                .description("Use if-statements for dynamic decision making")
                .difficulty(Level.Difficulty.MEDIUM)
                .concept("Conditional Branching (if / else)")
                .missionText("The corridor splits! Use a Python if-statement inside a loop (if step == 2:) to execute a turn_right() at step 2, then continue straight down to the goal portal!")
                .levelConfig("{\"rows\":5,\"cols\":5,\"playerStart\":{\"row\":0,\"col\":0},\"playerDirection\":\"E\",\"goal\":{\"row\":4,\"col\":2},\"walls\":[],\"gems\":[]}")
                .starterCode("# Loop 6 steps total through the corridor.\n# Use a conditional if-statement inside the loop to turn right at step 2.\n\n# TODO: Write your loop and conditional below\n")
                .solutionCode("# Loop 6 steps total; turn right at step 2 to navigate the fork\nfor step in range(6):\n    if step == 2:\n        turn_right()\n    move()")
                .xpReward(120)
                .coinReward(25)
                .orderIndex(5)
                .hints("[{\"text\":\"At step 2, the robot turns right to face South.\",\"cost\":0},{\"text\":\"Use if step == 2: followed by turn_right() inside the loop.\",\"cost\":10},{\"text\":\"for step in range(6):\\n    if step == 2:\\n        turn_right()\\n    move()\",\"cost\":15}]")
                .build());

        // Level 6 - Crystal Collector (Resource Collection)
        upsertLevel(world, Level.builder()
                .world(world)
                .title("Crystal Collector")
                .description("Gather energy crystals to power the portal")
                .difficulty(Level.Difficulty.MEDIUM)
                .concept("Resource Collection & Logic")
                .missionText("Three glowing energy crystals are scattered along the hallway! Move to each crystal's exact tile and call collect() to pick it up before entering the goal portal.")
                .levelConfig("{\"rows\":5,\"cols\":6,\"playerStart\":{\"row\":2,\"col\":0},\"playerDirection\":\"E\",\"goal\":{\"row\":2,\"col\":5},\"walls\":[],\"gems\":[{\"row\":2,\"col\":1},{\"row\":2,\"col\":3},{\"row\":2,\"col\":4}]}")
                .starterCode("# Collect all 3 crystals and reach the goal flag!\n# Use move() to walk and collect() when standing on a crystal.\n\n")
                .solutionCode("# Collect Gem 1 at (2, 1)\nmove()\ncollect()\n\n# Advance past (2, 2) and collect Gem 2 at (2, 3)\nmove()\nmove()\ncollect()\n\n# Advance and collect Gem 3 at (2, 4)\nmove()\ncollect()\n\n# Reach the goal portal at (2, 5)\nmove()")
                .xpReward(100)
                .coinReward(25)
                .orderIndex(6)
                .hints("[{\"text\":\"Crystals are located at columns 1, 3, and 4.\",\"cost\":0},{\"text\":\"Move forward, collect crystal 1, move 2 steps, collect crystal 2, move 1 step, collect crystal 3, move to portal.\",\"cost\":10},{\"text\":\"move() -> collect() -> move() x2 -> collect() -> move() -> collect() -> move()\",\"cost\":15}]")
                .build());

        // Level 7 - The Infinite Tunnel (While Loops)
        upsertLevel(world, Level.builder()
                .world(world)
                .title("The Infinite Tunnel")
                .description("Iterate dynamically using while-loops")
                .difficulty(Level.Difficulty.MEDIUM)
                .concept("While Loops & Counters")
                .missionText("A deep tunnel stretches ahead! Write a Python while loop using a counter variable (count = 0; while count < 5:) to advance 5 tiles to the light!")
                .levelConfig("{\"rows\":3,\"cols\":7,\"playerStart\":{\"row\":1,\"col\":0},\"playerDirection\":\"E\",\"goal\":{\"row\":1,\"col\":5},\"walls\":[],\"gems\":[]}")
                .starterCode("# Advance 5 tiles through the tunnel using a while loop and counter.\n\n# TODO: Initialize count = 0 and write a while loop checking when count < 5\n")
                .solutionCode("# While-loop with a counter variable to advance 5 tiles\ncount = 0\nwhile count < 5:\n    move()\n    count += 1")
                .xpReward(130)
                .coinReward(30)
                .orderIndex(7)
                .hints("[{\"text\":\"Initialize count = 0 before the loop.\",\"cost\":0},{\"text\":\"Write while count < 5: and increment count += 1 inside the loop.\",\"cost\":10},{\"text\":\"count = 0\\nwhile count < 5:\\n    move()\\n    count += 1\",\"cost\":15}]")
                .build());

        // Level 8 - Modular Patrol (Functions & Parameters)
        upsertLevel(world, Level.builder()
                .world(world)
                .title("Modular Patrol")
                .description("Define functions with parameters for custom step counts")
                .difficulty(Level.Difficulty.HARD)
                .concept("Functions with Parameters")
                .missionText("Define a reusable Python function def advance(n): that accepts a parameter n and moves the rover n steps. Call advance(3), turn_right(), then advance(3)!")
                .levelConfig("{\"rows\":5,\"cols\":5,\"playerStart\":{\"row\":0,\"col\":0},\"playerDirection\":\"E\",\"goal\":{\"row\":3,\"col\":3},\"walls\":[],\"gems\":[]}")
                .starterCode("# Define a reusable function advance(n) that moves the robot n times\ndef advance(n):\n    # TODO: Loop n times and call move()\n    pass\n\n# TODO: Use advance() and turn_right() to patrol to the goal portal\n")
                .solutionCode("# Define reusable helper function taking distance parameter 'n'\ndef advance(n):\n    for i in range(n):\n        move()\n\n# Patrol 3 steps East, turn South, then patrol 3 steps to the goal portal\nadvance(3)\nturn_right()\nadvance(3)")
                .xpReward(160)
                .coinReward(35)
                .orderIndex(8)
                .hints("[{\"text\":\"Inside def advance(n): write a for loop: for i in range(n): move()\",\"cost\":0},{\"text\":\"Call advance(3), turn_right(), advance(3).\",\"cost\":10},{\"text\":\"def advance(n):\\n    for i in range(n):\\n        move()\\n\\nadvance(3)\\nturn_right()\\nadvance(3)\",\"cost\":15}]")
                .build());

        // Level 9 - Boss: The Loop Dragon 🐉
        upsertLevel(world, Level.builder()
                .world(world)
                .title("Boss: The Loop Dragon 🐉")
                .description("Conquer the Dragon's lair in an epic coding challenge!")
                .difficulty(Level.Difficulty.HARD)
                .concept("Boss Level (Combined Concepts)")
                .missionText("BOSS FIGHT! The Loop Dragon guards the dark dungeon! You must collect all 3 elemental gems to disable the dragon's barrier and reach the exit portal (6,6). The central wall has a narrow opening at row 3. Plan your code carefully!")
                .levelConfig("{\"rows\":7,\"cols\":7,\"playerStart\":{\"row\":0,\"col\":0},\"playerDirection\":\"E\",\"goal\":{\"row\":6,\"col\":6},\"walls\":[{\"row\":0,\"col\":3},{\"row\":1,\"col\":3},{\"row\":2,\"col\":3},{\"row\":4,\"col\":3},{\"row\":5,\"col\":3},{\"row\":6,\"col\":3}],\"gems\":[{\"row\":0,\"col\":2},{\"row\":3,\"col\":3},{\"row\":6,\"col\":2}]}")
                .starterCode("# BOSS FIGHT! Collect all 3 gems and reach portal at (6,6).\n# Gems are located at (0,2), (3,3), and (6,2).\n# Notice: The wall column 3 has a gap at row 3!\n\n")
                .solutionCode("# --- Step 1: Advance 2 steps and collect Gem 1 at (0, 2) ---\nmove()\nmove()\ncollect()\n\n# --- Step 2: Turn South down column 2 to collect Gem 3 at (6, 2) ---\nturn_right()\nfor i in range(6):\n    move()\ncollect()\n\n# --- Step 3: Turn around and head North back to the wall opening at row 3 ---\nturn_left()\nturn_left()\nfor i in range(3):\n    move()\n\n# --- Step 4: Turn East, pass through the wall gap at (3, 3), and collect Gem 2 ---\nturn_right()\nmove()\ncollect()\nmove()\n\n# --- Step 5: Turn South down column 4 to row 6 ---\nturn_right()\nfor i in range(3):\n    move()\n\n# --- Step 6: Turn East and proceed to the Goal Portal at (6, 6) ---\nturn_left()\nmove()\nmove()")
                .xpReward(300)
                .coinReward(100)
                .orderIndex(9)
                .hints("[{\"text\":\"Step 1: Move East 2 steps to (0,2) and collect Gem 1.\",\"cost\":0},{\"text\":\"Step 2: Turn right (South) along column 2, move down 6 steps to (6,2) and collect Gem 3.\",\"cost\":15},{\"text\":\"Step 3: Turn around, walk North 3 steps to row 3, turn East to step through the wall gap at (3,3) for Gem 2, then head to (6,6)!\",\"cost\":25}]")
                .build());

        // Level 10 - Debugging: The Broken Rover 🔧
        upsertLevel(world, Level.builder()
                .world(world)
                .title("Debug: The Broken Rover 🔧")
                .description("Find and fix bugs in pre-existing code")
                .difficulty(Level.Difficulty.MEDIUM)
                .concept("Code Debugging & Syntax Verification")
                .missionText("A previous explorer left broken Python code on the rover terminal! It contains a syntax error (missing colon on line 5) and improper indentation. Inspect the code below, fix the errors, collect the gem at (2,2), and help the rover reach the goal!")
                .levelConfig("{\"rows\":5,\"cols\":6,\"playerStart\":{\"row\":2,\"col\":0},\"playerDirection\":\"E\",\"goal\":{\"row\":2,\"col\":5},\"walls\":[],\"gems\":[{\"row\":2,\"col\":2}]}")
                .starterCode("# DEBUG MISSION: Fix the syntax error (missing colon) and indentation!\nmove()\nmove()\ncollect()\nfor i in range(3)\nmove()")
                .solutionCode("# Step 1: Advance to (2, 2) and collect the energy gem\nmove()\nmove()\ncollect()\n\n# Step 2: Fixed for-loop with colon (:) and indentation to reach the portal\nfor i in range(3):\n    move()")
                .xpReward(125)
                .coinReward(40)
                .orderIndex(10)
                .hints("[{\"text\":\"Inspect line 5: In Python, for-loop headers must end with a colon (:).\",\"cost\":0},{\"text\":\"Change for i in range(3) to for i in range(3):\",\"cost\":10},{\"text\":\"Ensure move() on the line below the loop is indented by 4 spaces.\",\"cost\":15}]")
                .build());

        // Level 11 - Nested Fortress (Nested Loops)
        upsertLevel(world, Level.builder()
                .world(world)
                .title("Nested Fortress")
                .description("Master nested loops to navigate a fortress grid")
                .difficulty(Level.Difficulty.HARD)
                .concept("Nested Loops & Grid Traversal")
                .missionText("The Nested Fortress is a grid of corridors! You must traverse a zigzag path: move East 4 steps to (0,4), step down to row 2, move West 4 steps to (2,0), step down to row 4, and move East 4 steps to reach the exit portal at (4,4). Collect all 3 energy gems at the turn checkpoints!")
                .levelConfig("{\"rows\":5,\"cols\":5,\"playerStart\":{\"row\":0,\"col\":0},\"playerDirection\":\"E\",\"goal\":{\"row\":4,\"col\":4},\"walls\":[{\"row\":1,\"col\":1},{\"row\":1,\"col\":2},{\"row\":1,\"col\":3},{\"row\":3,\"col\":1},{\"row\":3,\"col\":2},{\"row\":3,\"col\":3}],\"gems\":[{\"row\":0,\"col\":4},{\"row\":2,\"col\":0},{\"row\":4,\"col\":4}]}")
                .starterCode("# Navigate the Nested Fortress in a zigzag sweep!\n# Collect all 3 gems at (0,4), (2,0), and (4,4) before reaching the portal.\n\n# TODO: Write loops to sweep the corridors and collect all gems\n")
                .solutionCode("# Sweep Row 0: East 4 steps, collect Gem 1 at (0,4)\nfor i in range(4):\n    move()\ncollect()\n\n# Step down to Row 2\nturn_right()\nfor i in range(2):\n    move()\nturn_right()\n\n# Sweep Row 2: West 4 steps, collect Gem 2 at (2,0)\nfor i in range(4):\n    move()\ncollect()\n\n# Step down to Row 4\nturn_left()\nfor i in range(2):\n    move()\nturn_left()\n\n# Sweep Row 4: East 4 steps, collect Gem 3 at (4,4) and reach Goal\nfor i in range(4):\n    move()\ncollect()")
                .xpReward(200)
                .coinReward(50)
                .orderIndex(11)
                .hints("[{\"text\":\"The fortress requires sweeping rows: move East 4 to (0,4), step down to row 2, sweep West 4 to (2,0), step down to row 4, and sweep East 4 to (4,4).\",\"cost\":0},{\"text\":\"Collect gems at (0,4), (2,0), and (4,4) at the end of each sweep.\",\"cost\":15},{\"text\":\"for i in range(4): move()\\ncollect()\\nturn_right()\\nfor i in range(2): move()\\nturn_right()\\nfor i in range(4): move()\\ncollect()\\nturn_left()\\nfor i in range(2): move()\\nturn_left()\\nfor i in range(4): move()\\ncollect()\",\"cost\":25}]")
                .build());

        // Level 12 - The Gem Gauntlet (Lists & Iteration)
        upsertLevel(world, Level.builder()
                .world(world)
                .title("The Gem Gauntlet")
                .description("Use lists to store and execute movement sequences")
                .difficulty(Level.Difficulty.HARD)
                .concept("Lists & Iteration")
                .missionText("The Gem Gauntlet scatters 5 energy crystals across a treacherous field! Store your segment distances in a Python list distances = [4, 2, 3, 4, 5] and iterate through it to navigate a winding path, collecting all gems along the way to reach the exit portal at (6,6)!")
                .levelConfig("{\"rows\":7,\"cols\":7,\"playerStart\":{\"row\":0,\"col\":0},\"playerDirection\":\"E\",\"goal\":{\"row\":6,\"col\":6},\"walls\":[{\"row\":1,\"col\":2},{\"row\":1,\"col\":3},{\"row\":3,\"col\":3},{\"row\":4,\"col\":3},{\"row\":5,\"col\":3}],\"gems\":[{\"row\":0,\"col\":4},{\"row\":2,\"col\":4},{\"row\":2,\"col\":1},{\"row\":6,\"col\":1},{\"row\":6,\"col\":6}]}")
                .starterCode("# The Gem Gauntlet — collect all 5 gems!\n# Store movement distances in a list and iterate through them.\n# Distances needed: [4, 2, 3, 4, 5]\n\n# TODO: Define your distances list and navigate through each segment\n")
                .solutionCode("# Distances for each segment: East 4, South 2, West 3, South 4, East 5\ndistances = [4, 2, 3, 4, 5]\n\n# Segment 1: East 4 to (0,4)\nfor i in range(distances[0]):\n    move()\ncollect()\n\n# Segment 2: South 2 to (2,4)\nturn_right()\nfor i in range(distances[1]):\n    move()\ncollect()\n\n# Segment 3: West 3 to (2,1)\nturn_right()\nfor i in range(distances[2]):\n    move()\ncollect()\n\n# Segment 4: South 4 to (6,1)\nturn_left()\nfor i in range(distances[3]):\n    move()\ncollect()\n\n# Segment 5: East 5 to (6,6)\nturn_left()\nfor i in range(distances[4]):\n    move()\ncollect()")
                .xpReward(220)
                .coinReward(55)
                .orderIndex(12)
                .hints("[{\"text\":\"Gems are located at (0,4), (2,4), (2,1), (6,1), and (6,6). Use distances = [4, 2, 3, 4, 5].\",\"cost\":0},{\"text\":\"Segment 1: East 4 -> Segment 2: South 2 -> Segment 3: West 3 -> Segment 4: South 4 -> Segment 5: East 5.\",\"cost\":15},{\"text\":\"distances = [4, 2, 3, 4, 5]\\nfor i in range(distances[0]): move()\\ncollect()\\nturn_right()\\nfor i in range(distances[1]): move()\\ncollect()\\nturn_right()\\nfor i in range(distances[2]): move()\\ncollect()\\nturn_left()\\nfor i in range(distances[3]): move()\\ncollect()\\nturn_left()\\nfor i in range(distances[4]): move()\\ncollect()\",\"cost\":25}]")
                .build());

        // Level 13 - Spiral Tower (Complex Algorithms)
        upsertLevel(world, Level.builder()
                .world(world)
                .title("Spiral Tower")
                .description("Navigate an inward-spiraling path using decreasing loops")
                .difficulty(Level.Difficulty.HARD)
                .concept("Spiral Algorithms & Decreasing Counters")
                .missionText("The Spiral Tower demands you trace a clockwise inward spiral! Start at (0,0), move East 5 steps, turn right, move South 5 steps, turn right, move West 5 steps, turn right, move North 4 steps, then head into the center portal at (2,3). Collect all 4 gems along the perimeter!")
                .levelConfig("{\"rows\":6,\"cols\":6,\"playerStart\":{\"row\":0,\"col\":0},\"playerDirection\":\"E\",\"goal\":{\"row\":2,\"col\":3},\"walls\":[],\"gems\":[{\"row\":0,\"col\":5},{\"row\":5,\"col\":5},{\"row\":5,\"col\":0},{\"row\":1,\"col\":1}]}")
                .starterCode("# Spiral Tower — trace a clockwise inward spiral!\n# Collect 4 gems at (0,5), (5,5), (5,0), and (1,1), then reach portal at (2,3).\n\n# TODO: Write your loop or movement commands to spiral inward\n")
                .solutionCode("# Trace a clockwise spiral inward\nsteps = 5\n\n# East 5 steps, collect Gem 1 at (0,5)\nfor i in range(steps):\n    move()\ncollect()\n\n# South 5 steps, collect Gem 2 at (5,5)\nturn_right()\nfor i in range(steps):\n    move()\ncollect()\n\n# West 5 steps, collect Gem 3 at (5,0)\nturn_right()\nfor i in range(steps):\n    move()\ncollect()\n\n# North 4 steps to (1,0)\nturn_right()\nfor i in range(4):\n    move()\n\n# East 1 step, collect Gem 4 at (1,1)\nturn_right()\nmove()\ncollect()\n\n# Navigate to center portal at (2,3)\nturn_right()\nmove()\nturn_left()\nfor i in range(2):\n    move()")
                .xpReward(250)
                .coinReward(60)
                .orderIndex(13)
                .hints("[{\"text\":\"A clockwise spiral moves East 5, South 5, West 5, North 4, then navigates to the center.\",\"cost\":0},{\"text\":\"Collect gems at (0,5), (5,5), (5,0), and (1,1) along the path.\",\"cost\":15},{\"text\":\"Move 5 East -> collect -> turn right -> 5 South -> collect -> turn right -> 5 West -> collect -> turn right -> 4 North -> turn right -> 1 East -> collect -> navigate to (2,3).\",\"cost\":25}]")
                .build());

        // Level 14 - The Mirror Labyrinth (Functions with Return Values)
        upsertLevel(world, Level.builder()
                .world(world)
                .title("The Mirror Labyrinth")
                .description("Define helper functions to navigate a symmetric maze")
                .difficulty(Level.Difficulty.HARD)
                .concept("Functions, Return Values & Code Reuse")
                .missionText("The Mirror Labyrinth has a symmetrical layout! Define a helper function def go_forward(n) that repeats move() n times. Use your function to navigate through the labyrinth, collecting 3 gems at (0,2), (3,6), and (7,5) while avoiding walls, and reach the exit at (7,7)!")
                .levelConfig("{\"rows\":8,\"cols\":8,\"playerStart\":{\"row\":0,\"col\":0},\"playerDirection\":\"E\",\"goal\":{\"row\":7,\"col\":7},\"walls\":[{\"row\":0,\"col\":3},{\"row\":1,\"col\":3},{\"row\":2,\"col\":3},{\"row\":4,\"col\":4},{\"row\":5,\"col\":4},{\"row\":6,\"col\":4}],\"gems\":[{\"row\":0,\"col\":2},{\"row\":3,\"col\":6},{\"row\":7,\"col\":5}]}")
                .starterCode("# The Mirror Labyrinth — define helper functions!\n# Define: def go_forward(n) to repeat move() n times\n# Use your helper to navigate cleanly around walls.\n\n# TODO: Define your helper function and navigate to (7,7)\n")
                .solutionCode("# Define helper function for clean navigation\ndef go_forward(n):\n    for i in range(n):\n        move()\n\n# Segment 1: East 2 steps, collect Gem 1 at (0,2)\ngo_forward(2)\ncollect()\n\n# Segment 2: Go South past wall to (3,2), then East to Gem 2 at (3,6)\nturn_right()\ngo_forward(3)\nturn_left()\ngo_forward(4)\ncollect()\n\n# Segment 3: South to row 7 (7,6)\nturn_right()\ngo_forward(4)\n\n# Segment 4: West to Gem 3 at (7,5) then collect\nturn_right()\nmove()\ncollect()\n\n# Segment 5: Turn around East and step into the goal portal at (7,7)\nturn_left()\nturn_left()\ngo_forward(2)")
                .xpReward(280)
                .coinReward(70)
                .orderIndex(14)
                .hints("[{\"text\":\"Define a helper function go_forward(n): for i in range(n): move()\",\"cost\":0},{\"text\":\"Route: East 2 to Gem 1 -> South 3, East 4 to Gem 2 -> South 4, West 1 to Gem 3 -> East 2 to Goal at (7,7).\",\"cost\":15},{\"text\":\"def go_forward(n):\\n    for i in range(n):\\n        move()\\n\\ngo_forward(2); collect(); turn_right(); go_forward(3); turn_left(); go_forward(4); collect(); turn_right(); go_forward(4); turn_right(); move(); collect(); turn_left(); turn_left(); go_forward(2)\",\"cost\":25}]")
                .build());

        // Level 15 - Final Boss: The Code Titan 🏆
        upsertLevel(world, Level.builder()
                .world(world)
                .title("Final Boss: The Code Titan 🏆")
                .description("The ultimate challenge combining every concept mastered!")
                .difficulty(Level.Difficulty.HARD)
                .concept("Grand Boss (All Concepts Combined)")
                .missionText("THE FINAL BOSS! The Code Titan has imprisoned 6 energy gems across a massive 9x9 dungeon! You must use EVERY skill you've learned — variables, loops, conditionals, functions, and collections — to navigate around obstacles, collect ALL 6 gems at (0,4), (4,8), (4,4), (4,0), (8,4), and (8,8), and reach the final victory portal at (8,8). The fate of CodeForest rests in your hands!")
                .levelConfig("{\"rows\":9,\"cols\":9,\"playerStart\":{\"row\":0,\"col\":0},\"playerDirection\":\"E\",\"goal\":{\"row\":8,\"col\":8},\"walls\":[{\"row\":1,\"col\":2},{\"row\":2,\"col\":2},{\"row\":1,\"col\":6},{\"row\":2,\"col\":6},{\"row\":6,\"col\":2},{\"row\":7,\"col\":2},{\"row\":6,\"col\":6},{\"row\":7,\"col\":6}],\"gems\":[{\"row\":0,\"col\":4},{\"row\":4,\"col\":8},{\"row\":4,\"col\":4},{\"row\":4,\"col\":0},{\"row\":8,\"col\":4},{\"row\":8,\"col\":8}]}")
                .starterCode("# ⚔️ FINAL BOSS: THE CODE TITAN ⚔️\n# Collect all 6 gems and reach the portal at (8,8)!\n# Gems are at: (0,4), (4,8), (4,4), (4,0), (8,4), and (8,8)\n#\n# Hint: Define an advance(n) function to move in 4-step increments!\n\n# TODO: Plan and execute your route through all 6 gems to victory!\n")
                .solutionCode("# Helper function to advance n steps\ndef advance(n):\n    for i in range(n):\n        move()\n\n# Gem 1 at (0,4): Advance East 4\nadvance(4)\ncollect()\n\n# Gem 2 at (4,8): Advance East 4 to (0,8), turn South, advance 4\nadvance(4)\nturn_right()\nadvance(4)\ncollect()\n\n# Gem 3 at (4,4) [Center]: Turn West, advance 4\nturn_right()\nadvance(4)\ncollect()\n\n# Gem 4 at (4,0): Continue West 4\nadvance(4)\ncollect()\n\n# Gem 5 at (8,4): Turn South, advance 4 to (8,0), turn East, advance 4\nturn_left()\nadvance(4)\nturn_left()\nadvance(4)\ncollect()\n\n# Gem 6 & Goal at (8,8): Continue East 4\nadvance(4)\ncollect()")
                .xpReward(500)
                .coinReward(200)
                .orderIndex(15)
                .hints("[{\"text\":\"Define an advance(n) function. Gems are located at (0,4), (4,8), (4,4), (4,0), (8,4), and (8,8).\",\"cost\":0},{\"text\":\"Use 4-step increments: East 4 (Gem 1) -> East 4, South 4 (Gem 2) -> West 4 (Gem 3) -> West 4 (Gem 4) -> South 4, East 4 (Gem 5) -> East 4 (Gem 6 & Goal).\",\"cost\":20},{\"text\":\"def advance(n):\\n    for i in range(n):\\n        move()\\n\\nadvance(4); collect(); advance(4); turn_right(); advance(4); collect(); turn_right(); advance(4); collect(); advance(4); collect(); turn_left(); advance(4); turn_left(); advance(4); collect(); advance(4); collect()\",\"cost\":35}]")
                .build());
    }

    private void createOrUpdateJavaLevels(World world) {
        // Level 1 - First Steps
        upsertLevel(world, Level.builder()
                .world(world)
                .title("First Steps")
                .description("Learn basic Java method invocation")
                .difficulty(Level.Difficulty.EASY)
                .concept("Sequencing & Method Statements")
                .missionText("WELCOME EXPLORER! Your robotic rover is ready for its first Java mission in CodeForest. The goal portal is 3 steps ahead. Write move(); statements ending with semicolons to reach the portal!")
                .levelConfig("{\"rows\":5,\"cols\":5,\"playerStart\":{\"row\":2,\"col\":0},\"playerDirection\":\"E\",\"goal\":{\"row\":2,\"col\":3},\"walls\":[],\"gems\":[]}")
                .starterCode("// Move the robot to the portal!\n// Each move(); statement advances 1 tile forward.\n\n// TODO: Write move(); statements to reach the portal\n")
                .solutionCode("// Move the robot 3 steps forward to the portal\nmove();\nmove();\nmove();")
                .xpReward(50)
                .coinReward(10)
                .orderIndex(1)
                .hints("[{\"text\":\"The robot starts on column 0. The goal portal is at column 3.\",\"cost\":0},{\"text\":\"Use the move(); command in Java (don't forget the semicolon).\",\"cost\":5},{\"text\":\"Write move(); three times — one per line.\",\"cost\":10}]")
                .build());

        // Level 2 - The Long Bridge
        upsertLevel(world, Level.builder()
                .world(world)
                .title("The Long Bridge")
                .description("Discover how Java for-loops repeat statements")
                .difficulty(Level.Difficulty.EASY)
                .concept("Java For Loops")
                .missionText("A long bridge stretches across a deep canyon — 6 tiles long! Instead of writing move(); six separate times, write a Java for-loop to repeat the action efficiently.")
                .levelConfig("{\"rows\":3,\"cols\":8,\"playerStart\":{\"row\":1,\"col\":0},\"playerDirection\":\"E\",\"goal\":{\"row\":1,\"col\":6},\"walls\":[{\"row\":0,\"col\":0},{\"row\":0,\"col\":1},{\"row\":0,\"col\":2},{\"row\":0,\"col\":3},{\"row\":0,\"col\":4},{\"row\":0,\"col\":5},{\"row\":0,\"col\":6},{\"row\":0,\"col\":7},{\"row\":2,\"col\":0},{\"row\":2,\"col\":1},{\"row\":2,\"col\":2},{\"row\":2,\"col\":3},{\"row\":2,\"col\":4},{\"row\":2,\"col\":5},{\"row\":2,\"col\":6},{\"row\":2,\"col\":7}],\"gems\":[]}")
                .starterCode("// Cross the 6-tile bridge using a Java for loop!\n// Repeat move(); 6 times across the bridge.\n\n// TODO: Write your Java for-loop below\n\n")
                .solutionCode("// Cross the 6-tile bridge using a for-loop\nfor (int i = 0; i < 6; i++) {\n    move();\n}")
                .xpReward(75)
                .coinReward(15)
                .orderIndex(2)
                .hints("[{\"text\":\"The bridge is a straight path 6 steps long.\",\"cost\":0},{\"text\":\"In Java, write: for(int i = 0; i < 6; i++) { move(); }\",\"cost\":5},{\"text\":\"for(int i = 0; i < 6; i++) {\\n    move();\\n}\",\"cost\":10}]")
                .build());

        // Level 3 - Variable Supply Run (Java Primitive Variables)
        upsertLevel(world, Level.builder()
                .world(world)
                .title("Variable Supply Run")
                .description("Store step distances inside Java int variables")
                .difficulty(Level.Difficulty.EASY)
                .concept("Variables & Primitive Assignment")
                .missionText("Welcome to the Variable Terminal! Store the distance in an integer variable named int steps = 4;, then use steps inside a for-loop to drive the rover to the supply depot.")
                .levelConfig("{\"rows\":3,\"cols\":6,\"playerStart\":{\"row\":1,\"col\":0},\"playerDirection\":\"E\",\"goal\":{\"row\":1,\"col\":4},\"walls\":[],\"gems\":[]}")
                .starterCode("// Store the distance in an int variable named 'steps' (4)\n// Then use 'steps' inside a for-loop to reach the supply depot.\n\n// TODO: Declare 'steps' and initialize it to 4\n\n// TODO: Loop using 'steps' to advance forward\n")
                .solutionCode("// Store the step count in a variable and loop through it\nint steps = 4;\nfor (int i = 0; i < steps; i++) {\n    move();\n}")
                .xpReward(80)
                .coinReward(20)
                .orderIndex(3)
                .hints("[{\"text\":\"Declare int steps = 4; on line 2.\",\"cost\":0},{\"text\":\"Write for(int i = 0; i < steps; i++) { move(); }\",\"cost\":5},{\"text\":\"int steps = 4;\\nfor(int i = 0; i < steps; i++) { move(); }\",\"cost\":10}]")
                .build());

        // Level 4 - The Maze
        upsertLevel(world, Level.builder()
                .world(world)
                .title("The Maze")
                .description("Navigate around dangerous wall obstacles")
                .difficulty(Level.Difficulty.MEDIUM)
                .concept("Direction Control & Turning Methods")
                .missionText("A heavy stone wall blocks your direct path! Use turnRight(); and turnLeft(); to change direction and navigate around the wall to reach the exit at (5,5).")
                .levelConfig("{\"rows\":6,\"cols\":6,\"playerStart\":{\"row\":0,\"col\":0},\"playerDirection\":\"E\",\"goal\":{\"row\":5,\"col\":5},\"walls\":[{\"row\":0,\"col\":3},{\"row\":1,\"col\":3},{\"row\":2,\"col\":3},{\"row\":3,\"col\":3}],\"gems\":[]}")
                .starterCode("// Navigate around the wall obstacle!\n// Available methods: move(); turnRight(); turnLeft();\n\n// TODO: Steer around the wall and reach the portal\n")
                .solutionCode("// Step 1: Advance 2 steps East to approach the obstacle\nmove();\nmove();\n\n// Step 2: Turn South to bypass the stone wall (rows 0 to 3)\nturnRight();\nfor (int i = 0; i < 4; i++) {\n    move();\n}\n\n// Step 3: Turn East and move 3 steps past the wall barrier\nturnLeft();\nfor (int i = 0; i < 3; i++) {\n    move();\n}\n\n// Step 4: Turn South and step into the goal portal at (5, 5)\nturnRight();\nmove();")
                .xpReward(100)
                .coinReward(20)
                .orderIndex(4)
                .hints("[{\"text\":\"Go right 2 steps, then call turnRight(); to face South.\",\"cost\":0},{\"text\":\"Walk down past row 3, call turnLeft(); to head East past column 3, then turn right to the goal.\",\"cost\":10},{\"text\":\"move(); x2 -> turnRight(); -> move(); x4 -> turnLeft(); -> move(); x3 -> turnRight(); -> move();\",\"cost\":15}]")
                .build());

        // Level 5 - The Forking Paths (Java If/Else Branching)
        upsertLevel(world, Level.builder()
                .world(world)
                .title("The Forking Paths")
                .description("Use Java if-statements for dynamic execution")
                .difficulty(Level.Difficulty.MEDIUM)
                .concept("Conditional Branching (if / else)")
                .missionText("The corridor splits! Write a Java if-statement inside a loop (if (step == 2) { turnRight(); }) to turn right at step 2, then continue straight down to the goal portal!")
                .levelConfig("{\"rows\":5,\"cols\":5,\"playerStart\":{\"row\":0,\"col\":0},\"playerDirection\":\"E\",\"goal\":{\"row\":4,\"col\":2},\"walls\":[],\"gems\":[]}")
                .starterCode("// Loop 6 steps total through the corridor.\n// Use a conditional if-statement inside the loop to turn right at step 2.\n\n// TODO: Write your for-loop and conditional below\n")
                .solutionCode("// Loop 6 steps total; turn right at step 2 to navigate the fork\nfor (int step = 0; step < 6; step++) {\n    if (step == 2) {\n        turnRight();\n    }\n    move();\n}")
                .xpReward(120)
                .coinReward(25)
                .orderIndex(5)
                .hints("[{\"text\":\"At step 2, call turnRight(); inside the if condition.\",\"cost\":0},{\"text\":\"if (step == 2) { turnRight(); }\",\"cost\":10},{\"text\":\"for(int step=0; step<6; step++) { if(step==2) turnRight(); move(); }\",\"cost\":15}]")
                .build());

        // Level 6 - Crystal Collector
        upsertLevel(world, Level.builder()
                .world(world)
                .title("Crystal Collector")
                .description("Gather energy crystals to power the portal")
                .difficulty(Level.Difficulty.MEDIUM)
                .concept("Resource Collection & Method Calls")
                .missionText("Three glowing energy crystals are scattered along the hallway! Move to each crystal's exact tile and call collect(); to pick it up before entering the goal portal.")
                .levelConfig("{\"rows\":5,\"cols\":6,\"playerStart\":{\"row\":2,\"col\":0},\"playerDirection\":\"E\",\"goal\":{\"row\":2,\"col\":5},\"walls\":[],\"gems\":[{\"row\":2,\"col\":1},{\"row\":2,\"col\":3},{\"row\":2,\"col\":4}]}")
                .starterCode("// Collect all 3 crystals and reach the goal flag!\n// Use move(); to walk and collect(); when standing on a crystal.\n\n")
                .solutionCode("// Collect Gem 1 at (2, 1)\nmove();\ncollect();\n\n// Advance past (2, 2) and collect Gem 2 at (2, 3)\nmove();\nmove();\ncollect();\n\n// Advance and collect Gem 3 at (2, 4)\nmove();\ncollect();\n\n// Reach the goal portal at (2, 5)\nmove();")
                .xpReward(100)
                .coinReward(25)
                .orderIndex(6)
                .hints("[{\"text\":\"Crystals are located at columns 1, 3, and 4.\",\"cost\":0},{\"text\":\"Move forward, collect crystal 1, move 2 steps, collect crystal 2, move 1 step, collect crystal 3, move to portal.\",\"cost\":10},{\"text\":\"move(); collect(); move(); move(); collect(); move(); collect(); move();\",\"cost\":15}]")
                .build());

        // Level 7 - The Infinite Tunnel (Java While Loops)
        upsertLevel(world, Level.builder()
                .world(world)
                .title("The Infinite Tunnel")
                .description("Iterate dynamically using Java while-loops")
                .difficulty(Level.Difficulty.MEDIUM)
                .concept("Java While Loops & Counters")
                .missionText("A deep tunnel stretches ahead! Write a Java while loop using a counter variable (int count = 0; while (count < 5) { move(); count++; }) to advance 5 tiles to the exit portal!")
                .levelConfig("{\"rows\":3,\"cols\":7,\"playerStart\":{\"row\":1,\"col\":0},\"playerDirection\":\"E\",\"goal\":{\"row\":1,\"col\":5},\"walls\":[],\"gems\":[]}")
                .starterCode("// Advance 5 tiles through the tunnel using a while loop and counter.\n\n// TODO: Declare int count = 0; and write a while loop checking when count < 5\n")
                .solutionCode("// While-loop with a counter variable to advance 5 tiles\nint count = 0;\nwhile (count < 5) {\n    move();\n    count++;\n}")
                .xpReward(130)
                .coinReward(30)
                .orderIndex(7)
                .hints("[{\"text\":\"Declare int count = 0; before the loop.\",\"cost\":0},{\"text\":\"Write while (count < 5) { move(); count++; }\",\"cost\":10},{\"text\":\"int count = 0; while (count < 5) { move(); count++; }\",\"cost\":15}]")
                .build());

        // Level 8 - Modular Patrol (Java Reusable Patrol)
        upsertLevel(world, Level.builder()
                .world(world)
                .title("Modular Patrol")
                .description("Use reusable loop blocks and variables for distance patrol")
                .difficulty(Level.Difficulty.HARD)
                .concept("Modular Logic & Parametric Loops")
                .missionText("Master parametric movement! Declare a distance variable int dist = 3; and use it to advance 3 steps East, call turnRight();, then advance 3 steps South to reach the goal!")
                .levelConfig("{\"rows\":5,\"cols\":5,\"playerStart\":{\"row\":0,\"col\":0},\"playerDirection\":\"E\",\"goal\":{\"row\":3,\"col\":3},\"walls\":[],\"gems\":[]}")
                .starterCode("// Declare an int variable 'dist' to store patrol distance (3)\n// Use 'dist' in your movement loops to reach the goal portal.\n\n// TODO: Declare int dist = 3;\n\n// TODO: Loop dist times East, turnRight(), then loop dist times South\n")
                .solutionCode("// Reusable distance variable for modular patrol\nint dist = 3;\n\n// Move 3 steps East\nfor (int i = 0; i < dist; i++) {\n    move();\n}\n\n// Turn South and move 3 steps to the goal portal\nturnRight();\nfor (int i = 0; i < dist; i++) {\n    move();\n}")
                .xpReward(160)
                .coinReward(35)
                .orderIndex(8)
                .hints("[{\"text\":\"Declare int dist = 3;\",\"cost\":0},{\"text\":\"Use dist in both for loops to move 3 steps East then 3 steps South.\",\"cost\":10},{\"text\":\"int dist=3; for(int i=0;i<dist;i++) move(); turnRight(); for(int i=0;i<dist;i++) move();\",\"cost\":15}]")
                .build());

        // Level 9 - Boss: The Loop Dragon 🐉
        upsertLevel(world, Level.builder()
                .world(world)
                .title("Boss: The Loop Dragon 🐉")
                .description("Conquer the Dragon's lair in an epic Java challenge!")
                .difficulty(Level.Difficulty.HARD)
                .concept("Boss Level (Combined Concepts)")
                .missionText("BOSS FIGHT! The Loop Dragon guards the dark dungeon! You must collect all 3 elemental gems to disable the dragon's barrier and reach the exit portal (6,6). The central wall has a narrow opening at row 3. Plan your code carefully!")
                .levelConfig("{\"rows\":7,\"cols\":7,\"playerStart\":{\"row\":0,\"col\":0},\"playerDirection\":\"E\",\"goal\":{\"row\":6,\"col\":6},\"walls\":[{\"row\":0,\"col\":3},{\"row\":1,\"col\":3},{\"row\":2,\"col\":3},{\"row\":4,\"col\":3},{\"row\":5,\"col\":3},{\"row\":6,\"col\":3}],\"gems\":[{\"row\":0,\"col\":2},{\"row\":3,\"col\":3},{\"row\":6,\"col\":2}]}")
                .starterCode("// BOSS FIGHT! Collect all 3 gems and reach portal at (6,6).\n// Gems are located at (0,2), (3,3), and (6,2).\n// Notice: The wall column 3 has a gap at row 3!\n\n")
                .solutionCode("// --- Step 1: Collect Gem 1 at (0, 2) ---\nmove();\nmove();\ncollect();\n\n// --- Step 2: Head South down column 2 to collect Gem 3 at (6, 2) ---\nturnRight();\nfor (int i = 0; i < 6; i++) {\n    move();\n}\ncollect();\n\n// --- Step 3: Turn around and head North back to the wall opening at row 3 ---\nturnLeft();\nturnLeft();\nfor (int i = 0; i < 3; i++) {\n    move();\n}\n\n// --- Step 4: Turn East, pass through the wall gap at (3, 3), and collect Gem 2 ---\nturnRight();\nmove();\ncollect();\nmove();\n\n// --- Step 5: Turn South down column 4 to row 6 ---\nturnRight();\nfor (int i = 0; i < 3; i++) {\n    move();\n}\n\n// --- Step 6: Turn East and proceed to the Goal Portal at (6, 6) ---\nturnLeft();\nmove();\nmove();")
                .xpReward(300)
                .coinReward(100)
                .orderIndex(9)
                .hints("[{\"text\":\"Step 1: Move East 2 steps to (0,2) and collect Gem 1.\",\"cost\":0},{\"text\":\"Step 2: Turn right (South) along column 2, move down 6 steps to (6,2) and collect Gem 3.\",\"cost\":15},{\"text\":\"Step 3: Turn around, walk North 3 steps to row 3, turn East to step through the wall gap at (3,3) for Gem 2, then head to (6,6)!\",\"cost\":25}]")
                .build());

        // Level 10 - Debugging: The Broken Rover 🔧
        upsertLevel(world, Level.builder()
                .world(world)
                .title("Debug: The Broken Rover 🔧")
                .description("Find and fix Java compilation errors")
                .difficulty(Level.Difficulty.MEDIUM)
                .concept("Code Debugging & Syntax Verification")
                .missionText("A previous explorer left broken Java code on the rover terminal! It contains a missing semicolon syntax error on line 3. Inspect the code below, fix line 3, collect the gem at (2,2), and help the rover reach the goal!")
                .levelConfig("{\"rows\":5,\"cols\":6,\"playerStart\":{\"row\":2,\"col\":0},\"playerDirection\":\"E\",\"goal\":{\"row\":2,\"col\":5},\"walls\":[],\"gems\":[{\"row\":2,\"col\":2}]}")
                .starterCode("// DEBUG MISSION: Fix the missing semicolon on line 3!\nmove();\nmove()\ncollect();\nfor (int i = 0; i < 3; i++) {\n    move();\n}")
                .solutionCode("// Step 1: Advance to (2, 2) and collect the energy gem (semicolon added to line 3)\nmove();\nmove();\ncollect();\n\n// Step 2: Advance through the final 3 steps into the exit portal\nfor (int i = 0; i < 3; i++) {\n    move();\n}")
                .xpReward(125)
                .coinReward(40)
                .orderIndex(10)
                .hints("[{\"text\":\"Java statements must end with a semicolon (;). Look at line 3.\",\"cost\":0},{\"text\":\"Change move() on line 3 to move();\",\"cost\":10},{\"text\":\"move(); move(); collect(); for(int i=0; i<3; i++) { move(); }\",\"cost\":15}]")
                .build());

        // Level 11 - Nested Fortress (Nested Loops)
        upsertLevel(world, Level.builder()
                .world(world)
                .title("Nested Fortress")
                .description("Master nested loops to navigate a fortress grid")
                .difficulty(Level.Difficulty.HARD)
                .concept("Nested Loops & Grid Traversal")
                .missionText("The Nested Fortress is a grid of corridors! You must traverse a zigzag path: move East 4 steps to (0,4), step down to row 2, move West 4 steps to (2,0), step down to row 4, and move East 4 steps to reach the exit portal at (4,4). Collect all 3 energy gems at the turn checkpoints!")
                .levelConfig("{\"rows\":5,\"cols\":5,\"playerStart\":{\"row\":0,\"col\":0},\"playerDirection\":\"E\",\"goal\":{\"row\":4,\"col\":4},\"walls\":[{\"row\":1,\"col\":1},{\"row\":1,\"col\":2},{\"row\":1,\"col\":3},{\"row\":3,\"col\":1},{\"row\":3,\"col\":2},{\"row\":3,\"col\":3}],\"gems\":[{\"row\":0,\"col\":4},{\"row\":2,\"col\":0},{\"row\":4,\"col\":4}]}")
                .starterCode("// Navigate the Nested Fortress in a zigzag sweep!\n// Collect all 3 gems at (0,4), (2,0), and (4,4) before reaching the portal.\n\n// TODO: Write loops to sweep the corridors and collect all gems\n")
                .solutionCode("// Sweep Row 0: East 4 steps, collect Gem 1 at (0,4)\nfor (int i = 0; i < 4; i++) { move(); }\ncollect();\n\n// Step down to Row 2\nturnRight();\nfor (int i = 0; i < 2; i++) { move(); }\nturnRight();\n\n// Sweep Row 2: West 4 steps, collect Gem 2 at (2,0)\nfor (int i = 0; i < 4; i++) { move(); }\ncollect();\n\n// Step down to Row 4\nturnLeft();\nfor (int i = 0; i < 2; i++) { move(); }\nturnLeft();\n\n// Sweep Row 4: East 4 steps, collect Gem 3 at (4,4) and reach Goal\nfor (int i = 0; i < 4; i++) { move(); }\ncollect();")
                .xpReward(200)
                .coinReward(50)
                .orderIndex(11)
                .hints("[{\"text\":\"The fortress requires sweeping rows: move East 4 to (0,4), step down to row 2, sweep West 4 to (2,0), step down to row 4, and sweep East 4 to (4,4).\",\"cost\":0},{\"text\":\"Collect gems at (0,4), (2,0), and (4,4) at the end of each sweep.\",\"cost\":15},{\"text\":\"for (int i=0; i<4; i++) move(); collect(); turnRight(); for (int i=0; i<2; i++) move(); turnRight(); for (int i=0; i<4; i++) move(); collect(); turnLeft(); for (int i=0; i<2; i++) move(); turnLeft(); for (int i=0; i<4; i++) move(); collect();\",\"cost\":25}]")
                .build());

        // Level 12 - The Gem Gauntlet (Arrays & Iteration)
        upsertLevel(world, Level.builder()
                .world(world)
                .title("The Gem Gauntlet")
                .description("Use arrays to store and execute movement sequences")
                .difficulty(Level.Difficulty.HARD)
                .concept("Arrays & Iteration")
                .missionText("The Gem Gauntlet scatters 5 energy crystals across a treacherous field! Store your turn distances in a Java array int[] distances = {4, 2, 3, 4, 5}; and iterate through it to navigate a winding path, collecting all gems along the way to reach the exit portal at (6,6)!")
                .levelConfig("{\"rows\":7,\"cols\":7,\"playerStart\":{\"row\":0,\"col\":0},\"playerDirection\":\"E\",\"goal\":{\"row\":6,\"col\":6},\"walls\":[{\"row\":1,\"col\":2},{\"row\":1,\"col\":3},{\"row\":3,\"col\":3},{\"row\":4,\"col\":3},{\"row\":5,\"col\":3}],\"gems\":[{\"row\":0,\"col\":4},{\"row\":2,\"col\":4},{\"row\":2,\"col\":1},{\"row\":6,\"col\":1},{\"row\":6,\"col\":6}]}")
                .starterCode("// The Gem Gauntlet — collect all 5 gems!\n// Store movement distances in an array and iterate through them.\n// Distances needed: {4, 2, 3, 4, 5}\n\n// TODO: Define your distances array and navigate through each segment\n")
                .solutionCode("// Distances for each segment: East 4, South 2, West 3, South 4, East 5\nint[] distances = {4, 2, 3, 4, 5};\n\n// Segment 1: East 4 to (0,4)\nfor (int i = 0; i < distances[0]; i++) { move(); }\ncollect();\n\n// Segment 2: South 2 to (2,4)\nturnRight();\nfor (int i = 0; i < distances[1]; i++) { move(); }\ncollect();\n\n// Segment 3: West 3 to (2,1)\nturnRight();\nfor (int i = 0; i < distances[2]; i++) { move(); }\ncollect();\n\n// Segment 4: South 4 to (6,1)\nturnLeft();\nfor (int i = 0; i < distances[3]; i++) { move(); }\ncollect();\n\n// Segment 5: East 5 to (6,6)\nturnLeft();\nfor (int i = 0; i < distances[4]; i++) { move(); }\ncollect();")
                .xpReward(220)
                .coinReward(55)
                .orderIndex(12)
                .hints("[{\"text\":\"Gems are located at (0,4), (2,4), (2,1), (6,1), and (6,6). Use int[] distances = {4, 2, 3, 4, 5};\",\"cost\":0},{\"text\":\"Segment 1: East 4 -> Segment 2: South 2 -> Segment 3: West 3 -> Segment 4: South 4 -> Segment 5: East 5.\",\"cost\":15},{\"text\":\"int[] distances = {4, 2, 3, 4, 5};\\nLoop through each segment, turning right for segments 2 & 3, and turning left for segments 4 & 5.\",\"cost\":25}]")
                .build());

        // Level 13 - Spiral Tower (Complex Algorithms)
        upsertLevel(world, Level.builder()
                .world(world)
                .title("Spiral Tower")
                .description("Navigate an inward-spiraling path using decreasing loops")
                .difficulty(Level.Difficulty.HARD)
                .concept("Spiral Algorithms & Decreasing Counters")
                .missionText("The Spiral Tower demands you trace a clockwise inward spiral! Start at (0,0), move East 5 steps, turn right, move South 5 steps, turn right, move West 5 steps, turn right, move North 4 steps, then head into the center portal at (2,3). Collect all 4 gems along the perimeter!")
                .levelConfig("{\"rows\":6,\"cols\":6,\"playerStart\":{\"row\":0,\"col\":0},\"playerDirection\":\"E\",\"goal\":{\"row\":2,\"col\":3},\"walls\":[],\"gems\":[{\"row\":0,\"col\":5},{\"row\":5,\"col\":5},{\"row\":5,\"col\":0},{\"row\":1,\"col\":1}]}")
                .starterCode("// Spiral Tower — trace a clockwise inward spiral!\n// Collect 4 gems at (0,5), (5,5), (5,0), and (1,1), then reach portal at (2,3).\n\n// TODO: Write your loop or movement commands to spiral inward\n")
                .solutionCode("// Trace a clockwise spiral inward\nint steps = 5;\n\n// East 5 steps, collect Gem 1 at (0,5)\nfor (int i = 0; i < steps; i++) { move(); }\ncollect();\n\n// South 5 steps, collect Gem 2 at (5,5)\nturnRight();\nfor (int i = 0; i < steps; i++) { move(); }\ncollect();\n\n// West 5 steps, collect Gem 3 at (5,0)\nturnRight();\nfor (int i = 0; i < steps; i++) { move(); }\ncollect();\n\n// North 4 steps to (1,0)\nturnRight();\nfor (int i = 0; i < 4; i++) { move(); }\n\n// East 1 step, collect Gem 4 at (1,1)\nturnRight();\nmove();\ncollect();\n\n// Navigate to center portal at (2,3)\nturnRight();\nmove();\nturnLeft();\nfor (int i = 0; i < 2; i++) { move(); }")
                .xpReward(250)
                .coinReward(60)
                .orderIndex(13)
                .hints("[{\"text\":\"A clockwise spiral moves East 5, South 5, West 5, North 4, then navigates to the center.\",\"cost\":0},{\"text\":\"Collect gems at (0,5), (5,5), (5,0), and (1,1) along the path.\",\"cost\":15},{\"text\":\"Move 5 East -> collect -> turn right -> 5 South -> collect -> turn right -> 5 West -> collect -> turn right -> 4 North -> turn right -> 1 East -> collect -> navigate to (2,3).\",\"cost\":25}]")
                .build());

        // Level 14 - The Mirror Labyrinth (Modular Code & Logic)
        upsertLevel(world, Level.builder()
                .world(world)
                .title("The Mirror Labyrinth")
                .description("Define helper methods to navigate a symmetric maze")
                .difficulty(Level.Difficulty.HARD)
                .concept("Methods, Modular Logic & Code Reuse")
                .missionText("The Mirror Labyrinth has a symmetrical layout! Use reusable loop-and-turn patterns with variables to efficiently navigate through the labyrinth, collecting 3 gems at (0,2), (3,6), and (7,5) while avoiding walls, and reach the exit at (7,7)!")
                .levelConfig("{\"rows\":8,\"cols\":8,\"playerStart\":{\"row\":0,\"col\":0},\"playerDirection\":\"E\",\"goal\":{\"row\":7,\"col\":7},\"walls\":[{\"row\":0,\"col\":3},{\"row\":1,\"col\":3},{\"row\":2,\"col\":3},{\"row\":4,\"col\":4},{\"row\":5,\"col\":4},{\"row\":6,\"col\":4}],\"gems\":[{\"row\":0,\"col\":2},{\"row\":3,\"col\":6},{\"row\":7,\"col\":5}]}")
                .starterCode("// The Mirror Labyrinth — use reusable code patterns!\n// Use variables and loops to navigate cleanly around walls.\n\n// TODO: Navigate around walls to collect all 3 gems and reach (7,7)\n")
                .solutionCode("// Segment 1: East 2 steps, collect Gem 1 at (0,2)\nint dist = 2;\nfor (int i = 0; i < dist; i++) { move(); }\ncollect();\n\n// Segment 2: Go South past wall to (3,2), then East to Gem 2 at (3,6)\nturnRight();\nfor (int i = 0; i < 3; i++) { move(); }\nturnLeft();\nfor (int i = 0; i < 4; i++) { move(); }\ncollect();\n\n// Segment 3: South to row 7 (7,6)\nturnRight();\nfor (int i = 0; i < 4; i++) { move(); }\n\n// Segment 4: West to Gem 3 at (7,5) then collect\nturnRight();\nmove();\ncollect();\n\n// Segment 5: Turn around East and step into the goal portal at (7,7)\nturnLeft();\nturnLeft();\nfor (int i = 0; i < 2; i++) { move(); }")
                .xpReward(280)
                .coinReward(70)
                .orderIndex(14)
                .hints("[{\"text\":\"Route: East 2 to Gem 1 -> South 3, East 4 to Gem 2 -> South 4, West 1 to Gem 3 -> East 2 to Goal at (7,7).\",\"cost\":0},{\"text\":\"The wall blocks column 3 (rows 0-2) and column 4 (rows 4-6). Plan a path that goes around both walls.\",\"cost\":15},{\"text\":\"dist=2; for(int i=0;i<dist;i++) move(); collect(); turnRight(); for(int i=0;i<3;i++) move(); turnLeft(); for(int i=0;i<4;i++) move(); collect(); turnRight(); for(int i=0;i<4;i++) move(); turnRight(); move(); collect(); turnLeft(); turnLeft(); for(int i=0;i<2;i++) move();\",\"cost\":25}]")
                .build());

        // Level 15 - Final Boss: The Code Titan 🏆
        upsertLevel(world, Level.builder()
                .world(world)
                .title("Final Boss: The Code Titan 🏆")
                .description("The ultimate challenge combining every concept mastered!")
                .difficulty(Level.Difficulty.HARD)
                .concept("Grand Boss (All Concepts Combined)")
                .missionText("THE FINAL BOSS! The Code Titan has imprisoned 6 energy gems across a massive 9x9 dungeon! You must use EVERY skill you've learned — variables, loops, conditionals, arrays, and modular code — to navigate around obstacles, collect ALL 6 gems at (0,4), (4,8), (4,4), (4,0), (8,4), and (8,8), and reach the final victory portal at (8,8). The fate of CodeForest rests in your hands!")
                .levelConfig("{\"rows\":9,\"cols\":9,\"playerStart\":{\"row\":0,\"col\":0},\"playerDirection\":\"E\",\"goal\":{\"row\":8,\"col\":8},\"walls\":[{\"row\":1,\"col\":2},{\"row\":2,\"col\":2},{\"row\":1,\"col\":6},{\"row\":2,\"col\":6},{\"row\":6,\"col\":2},{\"row\":7,\"col\":2},{\"row\":6,\"col\":6},{\"row\":7,\"col\":6}],\"gems\":[{\"row\":0,\"col\":4},{\"row\":4,\"col\":8},{\"row\":4,\"col\":4},{\"row\":4,\"col\":0},{\"row\":8,\"col\":4},{\"row\":8,\"col\":8}]}")
                .starterCode("// ⚔️ FINAL BOSS: THE CODE TITAN ⚔️\n// Collect all 6 gems and reach the portal at (8,8)!\n// Gems are at: (0,4), (4,8), (4,4), (4,0), (8,4), and (8,8)\n//\n// Hint: Use loops with 4-step increments to navigate the fortress!\n\n// TODO: Plan and execute your route through all 6 gems to victory!\n")
                .solutionCode("// Gem 1 at (0,4): Advance East 4\nfor (int i = 0; i < 4; i++) { move(); }\ncollect();\n\n// Gem 2 at (4,8): Advance East 4 to (0,8), turn South, advance 4\nfor (int i = 0; i < 4; i++) { move(); }\nturnRight();\nfor (int i = 0; i < 4; i++) { move(); }\ncollect();\n\n// Gem 3 at (4,4) [Center]: Turn West, advance 4\nturnRight();\nfor (int i = 0; i < 4; i++) { move(); }\ncollect();\n\n// Gem 4 at (4,0): Continue West 4\nfor (int i = 0; i < 4; i++) { move(); }\ncollect();\n\n// Gem 5 at (8,4): Turn South, advance 4 to (8,0), turn East, advance 4\nturnLeft();\nfor (int i = 0; i < 4; i++) { move(); }\nturnLeft();\nfor (int i = 0; i < 4; i++) { move(); }\ncollect();\n\n// Gem 6 & Goal at (8,8): Continue East 4\nfor (int i = 0; i < 4; i++) { move(); }\ncollect();")
                .xpReward(500)
                .coinReward(200)
                .orderIndex(15)
                .hints("[{\"text\":\"Gems are located at (0,4), (4,8), (4,4), (4,0), (8,4), and (8,8). Route uses 4-step intervals.\",\"cost\":0},{\"text\":\"Use 4-step increments: East 4 (Gem 1) -> East 4, South 4 (Gem 2) -> West 4 (Gem 3) -> West 4 (Gem 4) -> South 4, East 4 (Gem 5) -> East 4 (Gem 6 & Goal).\",\"cost\":20},{\"text\":\"for (int i=0; i<4; i++) move(); collect(); for (int i=0; i<4; i++) move(); turnRight(); for (int i=0; i<4; i++) move(); collect(); turnRight(); for (int i=0; i<4; i++) move(); collect(); for (int i=0; i<4; i++) move(); collect(); turnLeft(); for (int i=0; i<4; i++) move(); turnLeft(); for (int i=0; i<4; i++) move(); collect(); for (int i=0; i<4; i++) move(); collect();\",\"cost\":35}]")
                .build());
    }
}
