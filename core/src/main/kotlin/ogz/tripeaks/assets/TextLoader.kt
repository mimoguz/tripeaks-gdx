package ogz.tripeaks.assets

import com.badlogic.gdx.assets.AssetDescriptor
import com.badlogic.gdx.assets.AssetLoaderParameters
import com.badlogic.gdx.assets.AssetManager
import com.badlogic.gdx.assets.loaders.AsynchronousAssetLoader
import com.badlogic.gdx.assets.loaders.FileHandleResolver
import com.badlogic.gdx.files.FileHandle
import com.badlogic.gdx.utils.Array
import ktx.collections.GdxArray

/** Simple text asset loader for loading shader sources. */
class TextLoader(resolver: FileHandleResolver) :
    AsynchronousAssetLoader<Text, TextParameter>(resolver) {

    private var text: Text? = null

    override fun getDependencies(
        fileName: String?,
        file: FileHandle?,
        parameter: TextParameter?
    ): Array<AssetDescriptor<Any>> {
        return GdxArray.with()
    }

    override fun loadAsync(
        manager: AssetManager?,
        fileName: String?,
        file: FileHandle?,
        parameter: TextParameter?
    ) {
        text = if (file != null) Text(file) else Text()
    }

    override fun loadSync(
        manager: AssetManager?,
        fileName: String?,
        file: FileHandle?,
        parameter: TextParameter?
    ): Text {
        val result = text ?: Text()
        text = null
        return result
    }

}

class Text(val string: String) {

    constructor() : this("")
    constructor(handle: FileHandle) : this(String(handle.readBytes()))

}

class TextParameter : AssetLoaderParameters<Text>()