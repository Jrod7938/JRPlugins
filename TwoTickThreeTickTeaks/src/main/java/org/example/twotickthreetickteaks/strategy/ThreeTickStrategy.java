package org.example.twotickthreetickteaks.strategy;

import com.example.EthanApiPlugin.Collections.Inventory;
import com.example.EthanApiPlugin.Collections.TileObjects;
import com.example.EthanApiPlugin.Collections.Widgets;
import com.example.InteractionApi.InventoryInteraction;
import com.example.InteractionApi.TileObjectInteraction;
import com.example.Packets.MousePackets;
import com.example.Packets.MovementPackets;
import com.example.Packets.WidgetPackets;
import net.runelite.api.Client;
import net.runelite.api.TileObject;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.widgets.Widget;
import net.runelite.client.Notifier;
import org.apache.commons.lang3.tuple.Pair;
import org.example.twotickthreetickteaks.state.ThreeTickChopState;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class ThreeTickStrategy implements ChoppingStrategy {
    private static final int[] TEAK_TREE_IDS = {9036, 15062, 30437, 30438, 30439, 30440, 30441, 30442, 30443, 30444, 30445, 36686, 40758};
    private static final int[] TREE_STUMPS = {9037, 15063, 30446, 36687, 40759};

    @Inject
    private Client client;

    private boolean inProgress = false;
    private TileObject currentTree;
    private WorldPoint currentTreeWorldPoint;
    private Pair<WorldPoint, WorldPoint> teakTrees;
    private ThreeTickChopState state = ThreeTickChopState.WAITING_FOR_TREE_RESPAWN;

    @Override
    public void execute() {
        if (!inProgress) {
            switch (state) {
                case WAITING_FOR_TREE_RESPAWN:
                    waitForTreeRespawn();
                    break;
                case CHECK_SPECIAL_ATTACK:
                    checkSpecialAttack();
                    break;
                case USE_TAR_AND_DROP:
                    useTarAndDrop();
                    break;
                case CHOP_TREE:
                    chopTree();
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public void resetState() {
        state = ThreeTickChopState.WAITING_FOR_TREE_RESPAWN;
        teakTrees = null;
        currentTree = null;
    }

    private void waitForTreeRespawn() {
        inProgress = true;
        if (teakTrees == null) {
            Optional<Pair<WorldPoint, WorldPoint>> nearbyTrees = locateTreeWorldPoints();
            if (nearbyTrees.isEmpty()) {
                System.out.println("No trees found");

                inProgress = false;

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
            state = ThreeTickChopState.WAITING_FOR_TREE_RESPAWN;
            inProgress = false;

            return;
        }

        if (isTree(firstTree.get().getId())) {
            currentTree = firstTree.get();
            currentTreeWorldPoint = teakTrees.getLeft();

            state = ThreeTickChopState.CHECK_SPECIAL_ATTACK;
            inProgress = false;

            return;
        }

        if (isTree(secondTree.get().getId())) {
            currentTree = secondTree.get();
            currentTreeWorldPoint = teakTrees.getRight();
        }

        state = ThreeTickChopState.CHECK_SPECIAL_ATTACK;
        inProgress = false;
    }

    private void checkSpecialAttack() {
        inProgress = true;

        Optional<Widget> specOrbValue = Widgets
                .search()
                .withId(10485796)
                .first();
        Optional<Widget> specOrb = Widgets
                .search()
                .withId(10485795)
                .first();
        if (specOrbValue.isPresent() && specOrb.isPresent() && !specOrb.get().isHidden() && Objects.equals(specOrbValue.get().getText(), "100")) {
            MousePackets.queueClickPacket(0, 0);
            WidgetPackets.queueWidgetActionPacket(1, 10485795, -1, -1);
        }

        state = ThreeTickChopState.USE_TAR_AND_DROP;
        inProgress = false;
    }

    private void useTarAndDrop() {
        inProgress = true;
        Optional<Widget> guamLeaf = Inventory.search().nameContains("Guam leaf").first();
        if (guamLeaf.isEmpty()) {
            inProgress = false;

            return;
        }

        Optional<Widget> swampTar = Inventory.search().nameContains("Swamp tar").first();
        if (swampTar.isEmpty()) {
            inProgress = false;

            return;
        }

        WorldPoint localPlayerLocation = client.getLocalPlayer().getWorldLocation();

        MousePackets.queueClickPacket();
        MousePackets.queueClickPacket();
        WidgetPackets.queueWidgetOnWidget(guamLeaf.get(), swampTar.get());
        MousePackets.queueClickPacket();
        MovementPackets.queueMovement(localPlayerLocation);

        int logAmount = Inventory.search().nameContains("Teak logs").result().size();
        if (logAmount == 0) {
            state = ThreeTickChopState.CHOP_TREE;
            inProgress = false;

            return;
        }

        for (int i = 0; i < logAmount; i++) {
            Optional<Widget> teakLog = Inventory.search().nameContains("Teak logs").first();

            teakLog.ifPresent(widget -> InventoryInteraction.useItem(widget, "Drop"));
        }

        state = ThreeTickChopState.CHOP_TREE;
        inProgress = false;
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

                return;
            }

            TileObjectInteraction.interact(currentTreeState.get(), "Chop down");

            state = ThreeTickChopState.CHECK_SPECIAL_ATTACK;
        }

        inProgress = false;
    }

    private void reassignTree() {
        if (teakTrees == null) {
            Optional<Pair<WorldPoint, WorldPoint>> nearbyTrees = locateTreeWorldPoints();
            if (nearbyTrees.isEmpty()) {
                inProgress = false;

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

    private Optional<Pair<WorldPoint, WorldPoint>> locateTreeWorldPoints() {
        WorldPoint currentPlayerPosition = client.getLocalPlayer().getWorldLocation();
        List<TileObject> nearbyTrees = TileObjects
                .search()
                .nameContains("Teak")
                .filter(tree -> tree.getWorldLocation().distanceTo(currentPlayerPosition) <= 1)
                .result();

        List<TileObject> nearbyTrees2 = TileObjects
                .search()
                .nameContains("Teak")
                .result();

        if (nearbyTrees.size() < 2 || nearbyTrees.size() > 3) {
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
