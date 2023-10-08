package org.example.twotickthreetickteaks.strategy;

import com.example.EthanApiPlugin.Collections.Inventory;
import com.example.EthanApiPlugin.Collections.TileObjects;
import com.example.InteractionApi.InventoryInteraction;
import com.example.InteractionApi.TileObjectInteraction;
import com.example.Packets.MousePackets;
import com.example.Packets.MovementPackets;
import com.google.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.Skill;
import net.runelite.api.TileObject;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.widgets.Widget;
import net.runelite.client.Notifier;
import org.apache.commons.lang3.tuple.Pair;
import org.example.twotickthreetickteaks.state.TwoTickTeakState;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Slf4j
public class TwoTickStrategy implements ChoppingStrategy {
    private static final int[] TEAK_TREE_IDS = { 9036, 15062, 30437, 30438, 30439, 30440, 30441, 30442, 30443, 30444, 30445, 36686, 40758 };
    private static final int[] TREE_STUMPS = { 9037, 15063, 30446, 36687, 40759 };

    @Inject
    private Client client;
    @Inject
    private Notifier notifier;

    private TwoTickTeakState state = TwoTickTeakState.START;
    private boolean inProgress = false;
    private boolean synchronizedWithGameTicks = false;

    private int previousOrientation = -1;
    private TileObject currentTree;
    private WorldPoint currentTreeWorldPoint;

    private long previousWoodcuttingXp;
    private int idleXpTicks = 0;

    private Pair<WorldPoint, WorldPoint> teakTrees;

    @Override
    public void execute() {
        if (!inProgress) {
            if (!synchronizedWithGameTicks) {
                ensureSynchronizedWithGameTicks();
            }

            if (synchronizedWithGameTicks) {
                switch (state) {
                    case START:
                    case CHOPPING:
                        chopTree();
                        break;
                    case CLICKING_AWAY:
                        clickAwayFromTree();
                        break;
                    case DROPPING_LOGS:
                        dropLogs();
                        break;
                    default:
                        notifier.notify("Piggy teaks stopped unexpectedly!");
                        break;
                }
            }
        }

    }

    @Override
    public void resetState() {
        state = TwoTickTeakState.START;
        teakTrees = null;
        currentTree = null;
    }

    private void chopTree() {
        inProgress = true;

        if (currentTree == null && currentTreeWorldPoint == null) {
            reassignTree();
        }

        Optional<TileObject> currentTreeState = TileObjects.search().atLocation(currentTreeWorldPoint).first();
        if (currentTreeState.isPresent() && currentTree != null && currentTree.getCanvasTilePoly() != null) {
            if (isStump(currentTreeState.get().getId())) {
                reassignTree();
            }

            if (currentTree == null && currentTreeWorldPoint == null) {
                inProgress = false;
                synchronizedWithGameTicks = false;

                return;
            }

            TileObjectInteraction.interact(currentTreeState.get(), "Chop down");

            state = TwoTickTeakState.CLICKING_AWAY;
        }

        inProgress = false;
    }

    private void clickAwayFromTree() {
        inProgress = true;

        int currentWoodcuttingXp = client.getSkillExperience(Skill.WOODCUTTING);
        if (currentWoodcuttingXp == previousWoodcuttingXp) {
            idleXpTicks++;
        }

        if (currentWoodcuttingXp > previousWoodcuttingXp) {
            idleXpTicks = 0;
        }

        WorldPoint localPlayerLocation = client.getLocalPlayer().getWorldLocation();

        if (idleXpTicks > 15) {
            MousePackets.queueClickPacket();
            MovementPackets.queueMovement(localPlayerLocation);

            state = TwoTickTeakState.START;
            inProgress = false;
            synchronizedWithGameTicks = false;

            return;
        }

        MousePackets.queueClickPacket();
        MovementPackets.queueMovement(localPlayerLocation);

        dropLogs();

        previousWoodcuttingXp = client.getSkillExperience(Skill.WOODCUTTING);

        state = TwoTickTeakState.CHOPPING;
        inProgress = false;
    }

    private void dropLogs() {
        Optional<Widget> teakLog = Inventory.search().nameContains("Teak logs").first();
        teakLog.ifPresent(widget -> InventoryInteraction.useItem(widget, "Drop"));
    }

    private void reassignTree() {
        if (teakTrees == null) {
            Optional<Pair<WorldPoint, WorldPoint>> nearbyTrees = locateTreeWorldPoints();
            if (nearbyTrees.isEmpty()) {
                System.out.println("No trees found");
                notifier.notify("No trees found either side of you! Stopping script..");

                return;
            }

            teakTrees = nearbyTrees.get();
        }

        Optional<TileObject> firstTree = TileObjects.search().atLocation(teakTrees.getLeft()).first();
        Optional<TileObject> secondTree = TileObjects.search().atLocation(teakTrees.getRight()).first();
        if (firstTree.isEmpty() || secondTree.isEmpty()) {
            inProgress = false;

            return;
        }

        if (isStump(firstTree.get().getId()) && isStump(secondTree.get().getId())) {
            currentTree = null;
            currentTreeWorldPoint = null;

            return;
        }

        if (!isStump(firstTree.get().getId())) {
            currentTree = firstTree.get();
            currentTreeWorldPoint = teakTrees.getLeft();
        }

        if (!isStump(secondTree.get().getId())) {
            currentTree = secondTree.get();
            currentTreeWorldPoint = teakTrees.getRight();
        }
    }

    private void ensureSynchronizedWithGameTicks() {
        inProgress = true;

        int currentOrientation = client.getLocalPlayer().getOrientation();
        if ((previousOrientation == 1536 && currentOrientation == 512) || (previousOrientation == 512 && currentOrientation == 1536)) {
            synchronizedWithGameTicks = true;
            previousOrientation = -1;

            inProgress = false;

            return;
        }

        previousOrientation = currentOrientation;

        inProgress = false;
    }

    private Optional<Pair<WorldPoint, WorldPoint>> locateTreeWorldPoints() {
        WorldPoint currentPlayerPosition = client.getLocalPlayer().getWorldLocation();
        List<TileObject> nearbyTrees = TileObjects
                .search()
                .filter(tree -> isTree(tree.getId()) && tree.getWorldLocation().distanceTo(currentPlayerPosition) <= 1)
                .result();

        if (nearbyTrees.size() != 2) {
            return Optional.empty();
        }

        WorldPoint firstTree = nearbyTrees.get(0).getWorldLocation();
        WorldPoint secondTree = nearbyTrees.get(1).getWorldLocation();

        return Optional.of(Pair.of(firstTree, secondTree));
    }

    private boolean isTree(int id) {
        return Arrays.stream(TEAK_TREE_IDS).anyMatch(t -> t == id);
    }

    private boolean isStump(int id) {
        return Arrays.stream(TREE_STUMPS).anyMatch(t -> t == id);
    }
}
