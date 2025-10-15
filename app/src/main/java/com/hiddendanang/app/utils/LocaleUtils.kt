import android.content.Context
import android.content.ContextWrapper
import android.content.res.Configuration
import java.util.Locale

fun Context.wrapLocale(locale: Locale): ContextWrapper {
    val config = Configuration(resources.configuration)
    config.setLocale(locale)
    val newContext = createConfigurationContext(config)
    return ContextWrapper(newContext)
}
