package com.questWalker

import com.example.EthanApiPlugin.Collections.NPCs
import com.example.EthanApiPlugin.Collections.Widgets
import com.example.EthanApiPlugin.EthanApiPlugin
import com.example.InteractionApi.NPCInteraction
import com.example.InteractionApi.TileObjectInteraction
import com.example.PathingTesting.PathingTesting
import com.google.inject.Inject
import com.questhelper.QuestHelperPlugin
import com.questhelper.steps.ConditionalStep
import com.questhelper.steps.NpcStep
import com.questhelper.steps.ObjectStep
import com.questhelper.steps.QuestStep
import net.runelite.api.Client
import net.runelite.api.GameState
import net.runelite.api.NPC
import net.runelite.api.events.GameTick
import net.runelite.client.eventbus.Subscribe
import net.runelite.client.plugins.Plugin
import net.runelite.client.plugins.PluginDescriptor


@PluginDescriptor(
    name = "<html><font color=\"#9ddbff\">[JC]</font> Quest Walker </html>",
    description = "Walks to quest locations and interacts",
    tags = ["jc"],
    enabledByDefault = false
)
class QuestWalker : Plugin() {
    @Inject
    private lateinit var client: Client

    @Throws(Exception::class)
    override fun startUp() {
        if (client.gameState != GameState.LOGGED_IN) return

    }

    @Throws(Exception::class)
    override fun shutDown() {

    }

    @Subscribe
    fun onGameTick(e: GameTick) {
        if (client.gameState != GameState.LOGGED_IN) {
            EthanApiPlugin.stopPlugin(this)
            return
        }
        if (!Widgets.search().withTextContains("here to continue").empty()) return

        if (QuestHelperPlugin.getSelectedQuest() != null && !QuestHelperPlugin.getSelectedQuest().isCompleted) {
            if (!applyStep(null)) return // if we are not done with the step, return

            if (QuestHelperPlugin.getSelectedQuest().currentStep is ConditionalStep) {
                val conditionalStep: ConditionalStep =
                    QuestHelperPlugin.getSelectedQuest().currentStep as ConditionalStep
                for (step in conditionalStep.steps) {
                    applyStep(step)
                }
            }
        }

    }

    private fun applyStep(step: QuestStep?): Boolean {
        var questStep: QuestStep? = null
        questStep = step ?: QuestHelperPlugin.getSelectedQuest().currentStep

        if (questStep is ObjectStep) {
            return applyObjectStep(step)
        } else if (questStep is NpcStep) {
            return applyNpcStep(step)
        }
        return true
    }

    private fun applyNpcStep(step: QuestStep?): Boolean {
        var questStep: NpcStep? = null
        questStep = if (step != null) step as NpcStep?
        else QuestHelperPlugin.getSelectedQuest().currentStep as NpcStep

        val npcOptional = NPCs.search().withId(questStep!!.npcID).first()
        if (npcOptional.isPresent) {
            val npc: NPC = npcOptional.get()
            NPCInteraction.interact(questStep.npcID, "Talk-to")
        } else {
            if (questStep.worldPoint.distanceTo(client.localPlayer.worldLocation) > 2) {
                PathingTesting.walkTo(questStep.worldPoint)
                return false
            }
        }
        return true
    }


    private fun applyObjectStep(step: QuestStep?): Boolean {
        var questStep: ObjectStep? = null
        questStep = if (step != null) step as ObjectStep?
        else QuestHelperPlugin.getSelectedQuest().currentStep as ObjectStep

        val success: Boolean = TileObjectInteraction.interact(questStep!!.objectID)
        if (!success) {
            if (questStep.worldPoint.distanceTo(client.localPlayer.worldLocation) > 2) {
                PathingTesting.walkTo(questStep.worldPoint)
                return false
            }
        }
        return true
    }


}