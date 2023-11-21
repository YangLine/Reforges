package com.willfp.reforges.libreforge

import com.willfp.eco.core.config.interfaces.Config
import com.willfp.eco.util.containsIgnoreCase
import com.willfp.libreforge.Dispatcher
import com.willfp.libreforge.NoCompileData
import com.willfp.libreforge.ProvidedHolder
import com.willfp.libreforge.arguments
import com.willfp.libreforge.conditions.Condition
import com.willfp.libreforge.get
import com.willfp.reforges.reforges.Reforge
import com.willfp.reforges.util.ReforgeLookup
import org.bukkit.entity.Player

object ConditionHasReforge : Condition<NoCompileData>("has_reforge") {
    override val arguments = arguments {
        require("reforge", "You must specify the reforge!")
    }

    override fun isMet(
        dispatcher: Dispatcher<*>,
        config: Config,
        holder: ProvidedHolder,
        compileData: NoCompileData
    ): Boolean {
        val player = dispatcher.get<Player>() ?: return false

        return ReforgeLookup.provideReforges(player)
            .map { it.holder }
            .filterIsInstance<Reforge>()
            .map { it.id.key }
            .containsIgnoreCase(config.getString("reforge"))
    }
}
