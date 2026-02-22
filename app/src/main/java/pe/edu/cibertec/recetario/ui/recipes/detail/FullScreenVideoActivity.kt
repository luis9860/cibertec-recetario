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

class FullScreenVideoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFullScreenVideoBinding
    private var player: ExoPlayer? = null

    @OptIn(UnstableApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFullScreenVideoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Forzar Landscape
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE

        // Ocultar System UI
        hideSystemUI()

        val videoUrl = intent.getStringExtra("video_url") ?: return
        val token = intent.getStringExtra("token") ?: ""

        setupPlayer(videoUrl, token)

        // BOTÓN PARA SALIR DE PANTALLA COMPLETA
        binding.fullScreenPlayerView.setFullscreenButtonClickListener { isFullScreen ->
            // Como ya estamos en la actividad FullScreen, cualquier clic aquí significa que quiere SALIR
            finish()
        }
    }

    private fun hideSystemUI() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window, binding.root).let { controller ->
            controller.hide(WindowInsetsCompat.Type.systemBars())
            controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }

    @OptIn(UnstableApi::class)
    private fun setupPlayer(videoUrl: String, token: String) {
        val dataSourceFactory = DefaultHttpDataSource.Factory()
            .setDefaultRequestProperties(mapOf("Authorization" to "Bearer $token"))

        player = ExoPlayer.Builder(this)
            .setMediaSourceFactory(DefaultMediaSourceFactory(dataSourceFactory))
            .build()

        binding.fullScreenPlayerView.apply {
            player = this@FullScreenVideoActivity.player
            resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT
        }

        val mediaItem = MediaItem.fromUri(videoUrl)
        player?.setMediaItem(mediaItem)
        player?.prepare()
        player?.playWhenReady = true
    }

    override fun onPause() {
        super.onPause()
        player?.playWhenReady = false
    }

    override fun onDestroy() {
        super.onDestroy()
        player?.release()
        player = null
    }
}
