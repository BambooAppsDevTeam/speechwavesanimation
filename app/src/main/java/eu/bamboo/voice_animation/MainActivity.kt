package eu.bamboo.voice_animation

import android.Manifest
import android.os.Bundle
import android.view.Menu
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import eu.bamboo.voice_animation.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val neededPermissionsArray = listOfNotNull(
        Manifest.permission.RECORD_AUDIO
    ).toTypedArray()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        supportActionBar?.apply {
            title = "Voice Animations"
            setDisplayUseLogoEnabled(true)
            setDisplayShowHomeEnabled(true)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        menu?.findItem(R.id.permissions)?.isVisible = !appHasAudioRecordPermissions()
        return super.onCreateOptionsMenu(menu)
    }

    override fun onStart() {
        super.onStart()

        requestPermissionsIfNeeded()
    }

    private fun requestPermissionsIfNeeded() {
        if (!appHasAudioRecordPermissions()) {
            registerForActivityResult(
                ActivityResultContracts.RequestMultiplePermissions()
            ) { resultMap ->
                neededPermissionsArray
                    .all { resultMap[it] ?: false }
                    .then { hideWarning() }
            }.launch(neededPermissionsArray)
        }
        invalidateOptionsMenu()
    }

    private fun hideWarning() {
        invalidateOptionsMenu()
    }

    private fun appHasAudioRecordPermissions() = neededPermissionsArray
        .all { hasPermission(it) }
        .also {
            binding.navHostFragment.isVisible = it
            binding.warningText.isVisible = !it
        }

}