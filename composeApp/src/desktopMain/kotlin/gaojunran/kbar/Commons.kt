package gaojunran.kbar

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import kbar.composeapp.generated.resources.JetBrainsMono_Regular
import kbar.composeapp.generated.resources.Res
import org.jetbrains.compose.resources.Font


class MyStyles {
    companion object {
        val textFieldColor = Color(0xff1d1d1d)
        val surColor = Color(0xff262626)
        val textTitleColor = Color(0xffffffff)
        val textDescColor = Color(0x80ffffff)

        @Composable
        fun getMonoFontFamily(): FontFamily {
            return FontFamily(
                Font(Res.font.JetBrainsMono_Regular, FontWeight.Normal)
            )
        }
    }
}

class MyKeys{
    companion object{
        const val ALT_SPACE = "alt SPACE"
    }
}
