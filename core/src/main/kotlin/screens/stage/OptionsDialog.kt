package ogz.tripeaks.screens.stage

import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.ray3k.stripe.PopTable
import ogz.tripeaks.assets.BundleAssets
import ogz.tripeaks.assets.UiSkin
import ogz.tripeaks.assets.get
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
        add(Label(bundle["options"], skin))
        row()
        add(LabelButton(skin, bundle["save"]) {
            callback.invoke(OptionsDialogResult.Types.Apply(settingsData))
            hide()
        })
        debug = true
        isModal = true
        isHideOnUnfocus = false
    }
}

sealed interface OptionsDialogResult {
    companion object Types {
        data object Return : OptionsDialogResult
        data class Apply(val settingsData: SettingsData): OptionsDialogResult
    }
}

