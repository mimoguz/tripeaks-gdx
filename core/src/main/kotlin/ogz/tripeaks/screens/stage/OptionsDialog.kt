package ogz.tripeaks.screens.stage

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup
import com.badlogic.gdx.scenes.scene2d.ui.CheckBox
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup
import com.badlogic.gdx.scenes.scene2d.ui.Image
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox
import com.badlogic.gdx.scenes.scene2d.ui.Stack
import com.badlogic.gdx.scenes.scene2d.ui.Table
import com.badlogic.gdx.utils.Align
import com.ray3k.stripe.PopTable
import ktx.collections.toGdxArray
import ogz.tripeaks.assets.BundleAssets
import ogz.tripeaks.assets.TextureAtlasAssets
import ogz.tripeaks.assets.UiSkin
import ogz.tripeaks.assets.get
import ogz.tripeaks.Constants
import ogz.tripeaks.services.AnimationStrategies
import ogz.tripeaks.services.DrawingStrategies
import ogz.tripeaks.services.Layouts
import ogz.tripeaks.services.SettingsData
import ogz.tripeaks.ui.LabelButton

class OptionsDialog(
    skin: UiSkin,
    assets: AssetManager,
    settingsData: SettingsData,
    callback: (OptionsDialogResult) -> Unit
) : PopTable(skin) {

    init {
        val bundle = assets[BundleAssets.Bundle]
        val uiAssets = assets[TextureAtlasAssets.Ui]
        val cardAssets = assets[TextureAtlasAssets.Cards]

        val darkThemeSwitch = CheckBox(
            bundle["darkTheme"],
            skin
        ).apply {
            imageCell.padRight(Constants.UI_HORIZONTAL_SPACING)
            isChecked = settingsData.darkTheme
        }

        val showAllSwitch = CheckBox(
            bundle["showAll"],
            skin
        ).apply {
            imageCell.padRight(Constants.UI_HORIZONTAL_SPACING)
            isChecked = settingsData.drawingStrategy == DrawingStrategies.BackVisible
        }

        val emptyDiscardSwitch = CheckBox(
            bundle["emptyDiscard"],
            skin
        ).apply {
            imageCell.padRight(Constants.UI_HORIZONTAL_SPACING)
            isChecked = settingsData.emptyDiscard
        }

        val extraLineSpacing = MathUtils.floor(skin.extraLineSpacing / 2f).toFloat()

        val layoutSelection = SelectBox<String>(skin).apply {
            setAlignment(Align.center)
            style.listStyle.selection.apply {
                topHeight = Constants.TEXT_BUTTON_VERTICAL_PADDING + extraLineSpacing
                bottomHeight = Constants.TEXT_BUTTON_VERTICAL_PADDING + extraLineSpacing
                leftWidth = Constants.TEXT_BUTTON_HORIZONTAL_PADDING
                rightWidth = Constants.TEXT_BUTTON_HORIZONTAL_PADDING
            }
            items = Layouts.entries.map { bundle[it.name] }.toGdxArray()
            selectedIndex = Layouts.entries.indexOf(settingsData.layout)
        }

        val animationSelection = SelectBox<String>(skin).apply {
            setAlignment(Align.center)
            style.listStyle.selection.apply {
                topHeight = Constants.TEXT_BUTTON_VERTICAL_PADDING + extraLineSpacing
                bottomHeight = Constants.TEXT_BUTTON_VERTICAL_PADDING + extraLineSpacing
                leftWidth = Constants.TEXT_BUTTON_HORIZONTAL_PADDING
                rightWidth = Constants.TEXT_BUTTON_HORIZONTAL_PADDING
            }
            items = AnimationStrategies.entries.map { bundle[it.name] }.toGdxArray()
            selectedIndex = AnimationStrategies.entries.indexOf(settingsData.animation)
        }

        pad(
            Constants.UI_PANEL_VERTICAL_BORDER - 2f,
            Constants.UI_PANEL_HORIZONTAL_BORDER,
            Constants.UI_PANEL_VERTICAL_BORDER,
            Constants.UI_PANEL_HORIZONTAL_BORDER - Constants.UI_SCROLL_FIX,
        )

        // Title
        add(HorizontalGroup().apply {
            space(4f)
            children.add(Image(uiAssets.findRegion("${skin.resourcePrefix}_icon_options")))
            children.add(Label(bundle["options"], skin, UiSkin.TITLE_LABEL_STYLE))
        })
            .align(Align.left)
            .padLeft(-14f)
            .colspan(2)
            .padBottom(Constants.UI_VERTICAL_SPACING + skin.extraLineSpacing)

        row()

        val cardRegion = if (settingsData.darkTheme) "dark_card" else "light_card"

        val decorSelectList = (0..3).map { index ->
            CheckBox(null, skin, UiSkin.RADIO_BUTTON_STYLE).apply {
                isChecked = index == settingsData.backDesign
                add(Stack(
                    Image(cardAssets.findRegion(cardRegion)),
                    Image(cardAssets.findRegion("card_back", index))
                ))
                imageCell.align(Align.topLeft).padRight(2f)
            }
        }

        val decorGroup = ButtonGroup<CheckBox>().apply {
            for (sl in decorSelectList) {
                add(sl)
            }
            setMaxCheckCount(1)
            setMinCheckCount(1)
        }

        val decorSelect = Table().apply {
            add(Label(bundle["decor"], skin))
                .colspan(decorSelectList.size * 2)
                .padBottom(2f + skin.extraLineSpacing)
                .align(Align.left)

            row()

            for (item in decorSelectList) {
                add(item)
                    .align(Align.topLeft)
                    .padRight(Constants.UI_HORIZONTAL_SPACING * 2)
            }
        }

        val itemTable = Table().apply {
            defaults().align(Align.left).padBottom(3f + skin.extraLineSpacing).align(Align.left)
            padRight(Constants.UI_SCROLL_PUSH)

            add(Label(bundle["layout"], skin))
            row()
            add(layoutSelection)
                .padBottom(Constants.UI_VERTICAL_SPACING + skin.extraLineSpacing)
                .expandX()
                .fillX()

            row()

            add(Label(bundle["cardAnimation"], skin))
            row()
            add(animationSelection)
                .padBottom(Constants.UI_VERTICAL_SPACING + skin.extraLineSpacing)
                .expandX()
                .fillX()

            row()

            add(decorSelect).padBottom(Constants.UI_VERTICAL_SPACING + skin.extraLineSpacing)

            row()

            add(showAllSwitch).padBottom(Constants.UI_VERTICAL_SPACING + skin.extraLineSpacing)

            row()

            add(emptyDiscardSwitch).padBottom(Constants.UI_VERTICAL_SPACING + skin.extraLineSpacing)

            row()

            add(darkThemeSwitch).expandX()
        }

        add(ScrollPane(itemTable, skin).apply {
            setScrollBarTouch(true)
            setScrollbarsVisible(true)
            fadeScrollBars = false
        })
            .colspan(2)
            .padBottom(Constants.UI_VERTICAL_SPACING)
            .align(Align.left)
            .expand()

        row()

        add(LabelButton(skin, bundle["save"]) {
            val updatedSettings = settingsData.copy(
                darkTheme = darkThemeSwitch.isChecked,
                animation = AnimationStrategies.entries[animationSelection.selectedIndex],
                layout = Layouts.entries[layoutSelection.selectedIndex],
                emptyDiscard = emptyDiscardSwitch.isChecked,
                backDesign = decorGroup.checkedIndex,
                drawingStrategy =
                if (showAllSwitch.isChecked) DrawingStrategies.BackVisible
                else DrawingStrategies.BackHidden
            )
            callback.invoke(OptionsDialogResult.Types.Apply(updatedSettings))
            hide()
        })
            .expandX()
            .fillX()
            .uniformX()
            .padRight(Constants.UI_HORIZONTAL_SPACING / 2)

        add(LabelButton(skin, bundle["cancel"]) {
            callback.invoke(OptionsDialogResult.Types.Return)
            hide()
        })
            .expandX()
            .fillX()
            .uniformX()
            .padLeft(Constants.UI_HORIZONTAL_SPACING / 2)
            .padRight(Constants.UI_SCROLL_FIX)

        isModal = true
        isHideOnUnfocus = false
    }
}

sealed interface OptionsDialogResult {

    companion object Types {

        data object Return : OptionsDialogResult

        data class Apply(val settingsData: SettingsData) : OptionsDialogResult
    }
}

