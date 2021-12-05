package com.appsmoviles.gruposcomunitarios.presentation.map

import android.os.Bundle
import android.preference.PreferenceManager
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.appsmoviles.gruposcomunitarios.R
import com.appsmoviles.gruposcomunitarios.databinding.FragmentMapBinding
import com.appsmoviles.gruposcomunitarios.presentation.MainActivity
import dagger.hilt.android.AndroidEntryPoint

import org.osmdroid.config.Configuration.*
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.CustomZoomButtonsController
import org.osmdroid.views.overlay.gestures.RotationGestureOverlay
import org.osmdroid.views.overlay.compass.InternalCompassOrientationProvider

import org.osmdroid.views.overlay.compass.CompassOverlay
import org.osmdroid.views.overlay.Marker


@AndroidEntryPoint
class MapFragment : Fragment() {

    private val args: MapFragmentArgs by navArgs()

    private var _binding: FragmentMapBinding? = null
    private val binding: FragmentMapBinding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        getInstance().load(
            requireContext(),
            PreferenceManager.getDefaultSharedPreferences(requireContext())
        )
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMapBinding.inflate(inflater, container, false)

        (activity as AppCompatActivity).supportActionBar?.setTitle(R.string.fragment_map_title)

        binding.map.setTileSource(TileSourceFactory.MAPNIK)

        // Add rotation gestures overlay
        val rotationGestureOverlay = RotationGestureOverlay(binding.map)
        rotationGestureOverlay.isEnabled
        binding.map.setMultiTouchControls(true)
        binding.map.overlays.add(rotationGestureOverlay)

        // Enable compass overlay
        val compassOverlay =
            CompassOverlay(context, InternalCompassOrientationProvider(context), binding.map)
        compassOverlay.enableCompass()
        binding.map.overlays.add(compassOverlay)

        // Marker
        val startPoint = GeoPoint(args.location.latitude, args.location.longitude)
        val startMarker = Marker(binding.map)
        startMarker.position = startPoint
        startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        binding.map.overlays.add(startMarker)

        // Adjust zoom and set center position
        binding.map.controller.setCenter(startPoint)
        binding.map.controller.setZoom(17.5)
        binding.map.zoomController.setVisibility(CustomZoomButtonsController.Visibility.NEVER)

        return binding.root
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    override fun onResume() {
        (activity as MainActivity?)!!.displayHomeButton(true)
        binding.map.onResume()
        super.onResume()
    }

    override fun onPause() {
        binding.map.onPause()
        super.onPause()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            android.R.id.home -> findNavController().popBackStack()
        }

        return super.onOptionsItemSelected(item)
    }

    companion object {
        private const val TAG = "MapFragment"

        fun newInstance() = MapFragment()
    }
}