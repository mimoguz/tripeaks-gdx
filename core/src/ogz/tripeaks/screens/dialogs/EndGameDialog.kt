package ogz.tripeaks.screens.dialogs

import com.badlogic.gdx.scenes.scene2d.ui.Dialog
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.I18NBundle
import ogz.tripeaks.screens.controls.MyTextButton
import ogz.tripeaks.util.SkinData

class EndGameDialog(skinData: SkinData, theme: String, val res: I18NBundle) :
    Dialog("", skinData.skin, theme) {

    val removedLabel = Label("", skinData.skin, theme)
    val undoLabel = Label("", skinData.skin, theme)
    val chainLabel = Label("", skinData.skin, theme)
    val newGameButton = MyTextButton(res.get("newGameShort"), skinData, theme)
    val exitButton = MyTextButton(res.get("exit"), skinData, theme)

    init {
        buttonTable.pad(4f, 4f, 0f, 4f)
        buttonTable.defaults().width(110f)
        pad(16f, 24f, 16f, 24f)
        contentTable.apply {
            add(Label(res.get("won"), skinData.skin, theme))
            row()
            add(removedLabel).align(Align.left)
            row()
            add(undoLabel).align(Align.left)
            row()
            add(chainLabel).align(Align.left)
        }
        buttonTable.add(newGameButton)
        buttonTable.add(exitButton)
    }

    fun setRemovedFromStack(value: Int) {
        removedLabel.setText(res.format("fromStack", value))
    }

    fun setUsedUndo(value: Int) {
        removedLabel.setText(res.format("usedUndo", value))
    }

    fun setLongestChain(value: Int) {
        removedLabel.setText(res.format("longestChain", value))
    }

    fun setNewGameAction(value: () -> Unit) {
        newGameButton.setAction(value)
    }

    fun setExitAction(value: () -> Unit) {
        exitButton.setAction(value)
    }
}
