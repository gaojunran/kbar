package gaojunran.kbar

import java.awt.Image
import java.awt.SystemTray
import java.awt.TrayIcon
import java.awt.image.BufferedImage
import java.io.File
import java.net.URI
import java.net.URL
import javax.imageio.ImageIO

val tray = SystemTray.getSystemTray()

fun sendNotification(title: String, content: String) {

    println(SystemTray.isSupported())

    try{
//        println(System.getProperty("user.dir"))
        val icon = ImageIO.read(File("/home/nebula/Projects/kbar/composeApp/src/desktopMain/kotlin/gaojunran/kbar/resources/img.png"))
        val trayIcon = TrayIcon(icon, "NotificationHelper")
        tray.add(trayIcon)
        trayIcon.displayMessage(title, content, TrayIcon.MessageType.ERROR)
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

fun main() {
    sendNotification("Hello", "World")
}