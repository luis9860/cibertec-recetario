package pe.edu.cibertec.recetario.ui.recipes.detail

import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.annotation.OptIn
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.ui.AspectRatioFrameLayout
import pe.edu.cibertec.recetario.databinding.ActivityFullScreenVideoBinding

/**
 * Actividad dedicada para la reproducción de video en pantalla completa.
 * Se utiliza principalmente para videos en formato horizontal (landscape).
 */
class FullScreenVideoActivity : AppCompatActivity() {

    // ViewBinding para acceder al reproductor en el layout activity_full_screen_video.xml
    private lateinit var binding: ActivityFullScreenVideoBinding
    private var player: ExoPlayer? = null // Instancia del reproductor Media3

    @OptIn(UnstableApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFullScreenVideoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Forzar la orientación de la pantalla a horizontal (Landscape)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE

        // Ocultar las barras del sistema (estado y navegación) para una experiencia inmersiva
        hideSystemUI()

        // Obtener la URL del video y el token de seguridad pasados desde el fragmento anterior
        val videoUrl = intent.getStringExtra("video_url") ?: return
        val token = intent.getStringExtra("token") ?: ""

        // Configurar e iniciar el reproductor
        setupPlayer(videoUrl, token)

        // Configura el botón de pantalla completa del reproductor para cerrar la actividad y volver atrás
        binding.fullScreenPlayerView.setFullscreenButtonClickListener { isFullScreen ->
            // Como ya estamos en la actividad FullScreen, cualquier clic aquí significa que quiere SALIR
            finish()
        }
    }

    /**
     * Oculta las barras del sistema y configura el comportamiento de "swipe" para mostrarlas temporalmente.
     */
    private fun hideSystemUI() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window, binding.root).let { controller ->
            controller.hide(WindowInsetsCompat.Type.systemBars())
            controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }

    /**
     * Configura ExoPlayer con soporte para autenticación mediante Token.
     */
    @OptIn(UnstableApi::class)
    private fun setupPlayer(videoUrl: String, token: String) {
        // Crea una fábrica de datos que incluye el Token JWT en la cabecera "Authorization"
        val dataSourceFactory = DefaultHttpDataSource.Factory()
            .setDefaultRequestProperties(mapOf("Authorization" to "Bearer $token"))

        // Construye el reproductor con la configuración de red personalizada
        player = ExoPlayer.Builder(this)
            .setMediaSourceFactory(DefaultMediaSourceFactory(dataSourceFactory))
            .build()

        // Vincula el reproductor con la vista XML
        binding.fullScreenPlayerView.apply {
            player = this@FullScreenVideoActivity.player
            resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT
        }

        // Prepara el medio y comienza la reproducción automáticamente
        val mediaItem = MediaItem.fromUri(videoUrl)
        player?.setMediaItem(mediaItem)
        player?.prepare()
        player?.playWhenReady = true
    }

    // Pausa la reproducción si el usuario sale de la app o recibe una llamada
    override fun onPause() {
        super.onPause()
        player?.playWhenReady = false
    }

    /**
     * Libera los recursos del reproductor al cerrar la actividad.
     * Es fundamental para evitar fugas de memoria y consumo de batería.
     */
    override fun onDestroy() {
        super.onDestroy()
        player?.release()
        player = null
    }
}
