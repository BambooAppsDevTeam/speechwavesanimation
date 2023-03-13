package eu.bamboo.voice_animation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import eu.bamboo.voice_animation.databinding.FragmentMenuBinding

class MenuFragment : Fragment(R.layout.fragment_menu) {

    private var _binding: FragmentMenuBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMenuBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.animationList.onItemClickListener =
            AdapterView.OnItemClickListener { _, _, position, _ ->
                when (position) {
                    POSITION_ANIMATION_WAVE -> R.id.waveAnimationFragment
                    POSITION_ANIMATION_LINE -> R.id.lineAnimationFragment
                    else -> null
                }?.let { itemId ->
                    findNavController().navigate(itemId)
                }
            }
    }

    companion object {
        private const val POSITION_ANIMATION_WAVE = 0
        private const val POSITION_ANIMATION_LINE = 1
    }

}