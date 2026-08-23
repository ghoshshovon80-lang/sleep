/**
 * Metrolist Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.metrolist.music.ui.screens

import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.metrolist.music.LocalPlayerAwareWindowInsets
import com.metrolist.music.LocalPlayerConnection
import com.metrolist.music.R
import com.metrolist.music.extensions.toMediaItem
import com.metrolist.music.models.MediaMetadata
import com.metrolist.music.playback.queues.ListQueue
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

// ============================================================================
// DATA MODELS & PRESET CONTENT
// ============================================================================

data class InteractiveTrack(
    val id: String,
    val title: String,
    val artist: String,
    val durationSeconds: Int,
    val thumbnailUrl: String,
    val tag: String,
    val category: String = "90s",
    val language: String = "हिं"
)

data class InteractivePlaylist(
    val id: String,
    val title: String,
    val description: String,
    val coverUrl: String,
    val categoryBadge: String,
    val frequencyMHz: String = "101.4 MHz",
    val tracks: List<InteractiveTrack>
)

object InteractiveTravelData {
    val busDriverPlaylists = listOf(
        InteractivePlaylist(
            id = "travel_bus_90s_highway",
            title = "90s Highway Bus Express",
            description = "Overnight coach romantic classics & highway midnight beats",
            coverUrl = "https://images.unsplash.com/photo-1544620347-c4fd4a3d5957?w=600&auto=format&fit=crop&q=80",
            categoryBadge = "90s HIGHWAY",
            tracks = listOf(
                InteractiveTrack("lTRiuFIWV54", "Yeh Dil Aashiqana", "Alka Yagnik, Kumar Sanu", 355, "https://images.unsplash.com/photo-1544620347-c4fd4a3d5957?w=300&auto=format&fit=crop&q=80", "90s ROMANCE", "90s", "हिं"),
                InteractiveTrack("mPZkdNFkNps", "Rabb Kare Tujhko Bhi", "Udit Narayan, Alka Yagnik", 312, "https://images.unsplash.com/photo-1519692933481-e162a57d6721?w=300&auto=format&fit=crop&q=80", "SUPERHIT", "90s", "हिं"),
                InteractiveTrack("5qap5aO4i9A", "Tip Tip Barsa Paani", "Alka Yagnik, Udit Narayan", 345, "https://images.unsplash.com/photo-1509316975850-ff9c5deb0cd9?w=300&auto=format&fit=crop&q=80", "MONSOON CLASSIC", "15 AUG", "हिं"),
                InteractiveTrack("2gliGTOmjXY", "Sandese Aate Hai", "Roop Kumar Rathod, Sonu Nigam", 420, "https://images.unsplash.com/photo-1570125909232-eb263c188f7e?w=300&auto=format&fit=crop&q=80", "DESH BHAKTI", "15 AUG", "हिं"),
                InteractiveTrack("5wRWniH7rt8", "Chaiyya Chaiyya", "Sukhwinder Singh, Sapna Awasthi", 380, "https://images.unsplash.com/photo-1494515843206-f3117d3f51b7?w=300&auto=format&fit=crop&q=80", "HIGHWAY ANTHEM", "90s", "हिं")
            )
        ),
        InteractivePlaylist(
            id = "travel_bus_state_transport",
            title = "State Transport Regional",
            description = "Classic Marathi, Bangla & Bhojpuri highway passenger favorites",
            coverUrl = "https://images.unsplash.com/photo-1570125909232-eb263c188f7e?w=600&auto=format&fit=crop&q=80",
            categoryBadge = "REGIONAL BUS",
            tracks = listOf(
                InteractiveTrack("lTRiuFIWV54", "Ami Chini Go Chini Tomare", "Kishore Kumar", 310, "https://images.unsplash.com/photo-1570125909232-eb263c188f7e?w=300&auto=format&fit=crop&q=80", "RABINDRA SANGEET", "BANGLA / বাংলা", "বাংলা"),
                InteractiveTrack("mPZkdNFkNps", "Apsara Aali", "Ajay-Atul, Bela Shende", 290, "https://images.unsplash.com/photo-1544620347-c4fd4a3d5957?w=300&auto=format&fit=crop&q=80", "FOLK CLASSIC", "NOSTALGIC", "मरा"),
                InteractiveTrack("5qap5aO4i9A", "Rinkiya Ke Papa", "Manoj Tiwari", 250, "https://images.unsplash.com/photo-1509316975850-ff9c5deb0cd9?w=300&auto=format&fit=crop&q=80", "BHOJPURI EXPRESS", "BHOJPURI", "EN"),
                InteractiveTrack("2gliGTOmjXY", "Bela Bose", "Anjan Dutt", 340, "https://images.unsplash.com/photo-1519692933481-e162a57d6721?w=300&auto=format&fit=crop&q=80", "BANGLA RETRO", "BANGLA / বাংলা", "বাংলা")
            )
        ),
        InteractivePlaylist(
            id = "travel_bus_monsoon",
            title = "Monsoon Night Journey",
            description = "Steady highway rain, asphalt hiss & melancholic gold",
            coverUrl = "https://images.unsplash.com/photo-1519692933481-e162a57d6721?w=600&auto=format&fit=crop&q=80",
            categoryBadge = "NIGHT RAIN",
            tracks = listOf(
                InteractiveTrack("mPZkdNFkNps", "Pardesi Pardesi", "Udit Narayan, Alka Yagnik", 410, "https://images.unsplash.com/photo-1519692933481-e162a57d6721?w=300&auto=format&fit=crop&q=80", "LONG DRIVE", "NOSTALGIC", "हिं"),
                InteractiveTrack("lTRiuFIWV54", "Ghar Aaja Pardesi", "Manpreet Kaur, Pamela Chopra", 390, "https://images.unsplash.com/photo-1509316975850-ff9c5deb0cd9?w=300&auto=format&fit=crop&q=80", "DDLJ CLASSIC", "NOSTALGIC", "हिं"),
                InteractiveTrack("5qap5aO4i9A", "Kuch Kuch Hota Hai", "Udit Narayan, Alka Yagnik", 295, "https://images.unsplash.com/photo-1544620347-c4fd4a3d5957?w=300&auto=format&fit=crop&q=80", "EVERGREEN", "90s", "हिं"),
                InteractiveTrack("5wRWniH7rt8", "Tere Naam", "Udit Narayan, Alka Yagnik", 380, "https://images.unsplash.com/photo-1494515843206-f3117d3f51b7?w=300&auto=format&fit=crop&q=80", "SAD MELODY", "90s", "हिं")
            )
        )
    )

    val deluxeSaloonPlaylists = listOf(
        InteractivePlaylist(
            id = "travel_saloon_vintage",
            title = "Purane Yaadien wo • 2000s Ke Haircut",
            description = "Barbershop lounge, scissors rhythm & radio gold",
            coverUrl = "https://images.unsplash.com/photo-1503951914875-452162b0f3f1?w=600&auto=format&fit=crop&q=80",
            categoryBadge = "VINTAGE SALOON",
            frequencyMHz = "101.4 MHz",
            tracks = listOf(
                InteractiveTrack("5wRWniH7rt8", "O yaaron maaf karna...", "Purane Yaadien wo", 252, "https://images.unsplash.com/photo-1503951914875-452162b0f3f1?w=300&auto=format&fit=crop&q=80", "2000s HAIRCUT"),
                InteractiveTrack("2gliGTOmjXY", "Roop Tera Mastana", "Kishore Kumar", 220, "https://images.unsplash.com/photo-1585747860715-2ba37e788b70?w=300&auto=format&fit=crop&q=80", "SUNDAY SPECIAL"),
                InteractiveTrack("lTRiuFIWV54", "Kya Hua Tera Wada", "Mohammed Rafi", 260, "https://images.unsplash.com/photo-1621605815971-fbc98d665033?w=300&auto=format&fit=crop&q=80", "GOLDEN ERA"),
                InteractiveTrack("mPZkdNFkNps", "Khaike Paan Banaraswala", "Kishore Kumar", 230, "https://images.unsplash.com/photo-1599351431202-1e0f0137899a?w=300&auto=format&fit=crop&q=80", "DESI GROOVE")
            )
        ),
        InteractivePlaylist(
            id = "travel_saloon_gentleman",
            title = "Retro Gentleman's Radio",
            description = "Warm tube amp jazz & Sunday morning lounge",
            coverUrl = "https://images.unsplash.com/photo-1585747860715-2ba37e788b70?w=600&auto=format&fit=crop&q=80",
            categoryBadge = "BARBER FM",
            frequencyMHz = "98.2 MHz",
            tracks = listOf(
                InteractiveTrack("2gliGTOmjXY", "Mere Samne Wali Khidki Mein", "Kishore Kumar", 210, "https://images.unsplash.com/photo-1585747860715-2ba37e788b70?w=300&auto=format&fit=crop&q=80", "CLASSIC LOUNGE"),
                InteractiveTrack("5wRWniH7rt8", "Eena Meena Deeka", "Kishore Kumar", 195, "https://images.unsplash.com/photo-1503951914875-452162b0f3f1?w=300&auto=format&fit=crop&q=80", "RETRO BEATS"),
                InteractiveTrack("5qap5aO4i9A", "Pyar Diwana Hota Hai", "Kishore Kumar", 270, "https://images.unsplash.com/photo-1621605815971-fbc98d665033?w=300&auto=format&fit=crop&q=80", "AFTERNOON CHILL"),
                InteractiveTrack("lTRiuFIWV54", "Chura Liya Hai Tumne", "Asha Bhosle, Mohammed Rafi", 285, "https://images.unsplash.com/photo-1599351431202-1e0f0137899a?w=300&auto=format&fit=crop&q=80", "ROMANTIC SHAVE")
            )
        )
    )

    val saloonQuotes = listOf(
        "\"Aaj Sunday hai! 1 ghante ki waiting hai, 4 baje ajaana beta. Abhi bahot number hai\" \n— Saloon Pe Betha Taau",
        "\"Bhaiya side se thoda chhota kar do, upar se mat chhoona bilkul!\" \n— School Kid",
        "\"Ustad, mustard oil se 10 minute champi kar do ekdum kadak!\" \n— Regular Customer",
        "\"Dadi setting ekdum sharp honi chahiye, Shaadi hai aaj shaam ko\" \n— Groom-to-be",
        "\"Alum (Phitkari) lagata hu, thoda jale-ga par face fresh ho jayega\" \n— Saloon Master"
    )

    val busRoutes = listOf(
        "NH 48 • DELHI - MUMBAI",
        "NH 19 • VARANASI - PATNA",
        "NH 44 • KANYAKUMARI - SRINAGAR",
        "NH 66 • MUMBAI - GOA"
    )
}

// Native audio synthesizers for sound FX
private fun playPneumaticHornSound() {
    Thread {
        try {
            val sampleRate = 44100
            val durationMs = 650
            val numSamples = sampleRate * durationMs / 1000
            val sample = DoubleArray(numSamples)
            val buffer = ShortArray(numSamples)

            val freqs = doubleArrayOf(440.0, 554.37, 659.25, 880.0)

            for (i in 0 until numSamples) {
                val t = i.toDouble() / sampleRate
                val mod = 1.0 + 0.12 * sin(2.0 * Math.PI * 16.0 * t)
                var env = 1.0
                if (i < 2000) env = i / 2000.0
                else if (i > numSamples - 4000) env = (numSamples - i) / 4000.0

                var wave = 0.0
                for (f in freqs) {
                    wave += sin(2.0 * Math.PI * f * t * mod)
                }
                sample[i] = (wave / freqs.size) * env
                buffer[i] = (sample[i] * 30000).toInt().coerceIn(-32768, 32767).toShort()
            }

            @Suppress("DEPRECATION")
            val audioTrack = AudioTrack(
                AudioManager.STREAM_MUSIC,
                sampleRate,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT,
                numSamples * 2,
                AudioTrack.MODE_STATIC
            )
            audioTrack.write(buffer, 0, buffer.size)
            audioTrack.play()
            Thread.sleep(durationMs.toLong() + 100)
            audioTrack.release()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }.start()
}

private fun playScissorSnipSound() {
    Thread {
        try {
            val sampleRate = 44100
            val durationMs = 120
            val numSamples = sampleRate * durationMs / 1000
            val buffer = ShortArray(numSamples)
            val random = Random(System.currentTimeMillis())

            for (i in 0 until numSamples) {
                val envelope = Math.exp(-i.toDouble() / (sampleRate * 0.015))
                val metallicNoise = (random.nextDouble() * 2.0 - 1.0) * envelope
                val tone = sin(2.0 * Math.PI * 3500.0 * (i.toDouble() / sampleRate)) * envelope * 0.5
                buffer[i] = ((metallicNoise + tone) * 22000).toInt().coerceIn(-32768, 32767).toShort()
            }

            @Suppress("DEPRECATION")
            val audioTrack = AudioTrack(
                AudioManager.STREAM_MUSIC,
                sampleRate,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT,
                numSamples * 2,
                AudioTrack.MODE_STATIC
            )
            audioTrack.write(buffer, 0, buffer.size)
            audioTrack.play()
            Thread.sleep(durationMs.toLong() + 50)
            audioTrack.release()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }.start()
}

// ============================================================================
// MAIN SCREEN ENTRYPOINT
// ============================================================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TravelScreen(
    navController: NavController
) {
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val playerConnection = LocalPlayerConnection.current
    val currentMediaItem = playerConnection?.player?.currentMediaItem
    val isPlayingState = playerConnection?.isPlaying?.collectAsState()
    val isPlaying = isPlayingState?.value == true

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = stringResource(R.string.travel_experience),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = stringResource(R.string.travel_subtitle),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            painter = painterResource(R.drawable.arrow_back),
                            contentDescription = stringResource(R.string.back)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            TabRow(
                selectedTabIndex = selectedTabIndex,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.primary,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            ) {
                Tab(
                    selected = selectedTabIndex == 0,
                    onClick = { selectedTabIndex = 0 },
                    text = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.explore_outlined),
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = stringResource(R.string.tab_bus_driver),
                                fontWeight = if (selectedTabIndex == 0) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                )
                Tab(
                    selected = selectedTabIndex == 1,
                    onClick = { selectedTabIndex = 1 },
                    text = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.radio),
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = stringResource(R.string.tab_deluxe_saloon),
                                fontWeight = if (selectedTabIndex == 1) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                )
            }

            AnimatedContent(
                targetState = selectedTabIndex,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "travel_interactive_transition"
            ) { tabIndex ->
                if (tabIndex == 0) {
                    BusDriverInteractiveDashboard(
                        currentMediaId = currentMediaItem?.mediaId,
                        isPlaying = isPlaying,
                        onPlayPlaylistTrack = { playlist, trackIndex ->
                            val mediaItems = playlist.tracks.map { track ->
                                MediaMetadata(
                                    id = track.id,
                                    title = track.title,
                                    artists = listOf(MediaMetadata.Artist(id = null, name = track.artist)),
                                    duration = track.durationSeconds,
                                    thumbnailUrl = track.thumbnailUrl,
                                    album = MediaMetadata.Album(id = playlist.id, title = playlist.title)
                                ).toMediaItem()
                            }
                            playerConnection?.playQueue(
                                ListQueue(
                                    title = playlist.title,
                                    items = mediaItems,
                                    startIndex = trackIndex
                                )
                            )
                        }
                    )
                } else {
                    DeluxeSaloonInteractiveTransistor(
                        currentMediaId = currentMediaItem?.mediaId,
                        isPlaying = isPlaying,
                        onPlayPlaylistTrack = { playlist, trackIndex ->
                            val mediaItems = playlist.tracks.map { track ->
                                MediaMetadata(
                                    id = track.id,
                                    title = track.title,
                                    artists = listOf(MediaMetadata.Artist(id = null, name = track.artist)),
                                    duration = track.durationSeconds,
                                    thumbnailUrl = track.thumbnailUrl,
                                    album = MediaMetadata.Album(id = playlist.id, title = playlist.title)
                                ).toMediaItem()
                            }
                            playerConnection?.playQueue(
                                ListQueue(
                                    title = playlist.title,
                                    items = mediaItems,
                                    startIndex = trackIndex
                                )
                            )
                        }
                    )
                }
            }
        }
    }
}

// ============================================================================
// TAB 1: 90s BUS DRIVER (1:1 PIXEL PERFECT REPLICA OF busdriver.wtf)
// ============================================================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BusDriverInteractiveDashboard(
    currentMediaId: String?,
    isPlaying: Boolean,
    onPlayPlaylistTrack: (InteractivePlaylist, Int) -> Unit
) {
    var selectedPlaylistIndex by remember { mutableIntStateOf(0) }
    var selectedRouteIndex by remember { mutableIntStateOf(0) }
    var selectedCategoryChip by remember { mutableStateOf("90s") }
    var selectedLanguage by remember { mutableStateOf("हिं") }

    val selectedPlaylist = InteractiveTravelData.busDriverPlaylists[selectedPlaylistIndex]

    // Horn FX state & animation
    var hornFlashing by remember { mutableStateOf(false) }

    // Live clock state
    var currentTimeString by remember { mutableStateOf("23:57 55") }
    var passengerCount by remember { mutableIntStateOf(104) }

    // Bottom sheet state for expandable full playlist
    var showPlaylistSheet by remember { mutableStateOf(false) }

    val haptic = LocalHapticFeedback.current

    // Real-time ticking clock & passenger counter fluctuation
    LaunchedEffect(Unit) {
        val formatter = SimpleDateFormat("HH:mm ss", Locale.getDefault())
        while (true) {
            currentTimeString = formatter.format(Date())
            if (Random.nextInt(0, 10) > 7) {
                passengerCount = (passengerCount + Random.nextInt(-2, 3)).coerceIn(45, 120)
            }
            delay(1000L)
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "bus_anim")

    // Road motion offset
    val roadOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = if (isPlaying) 800 else 2500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "road_offset"
    )

    // Bus wheel rotation angle
    val wheelRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = if (isPlaying) 400 else 2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "wheel_rotation"
    )

    // Bus suspension bounce offset
    val suspensionBounce by infiniteTransition.animateFloat(
        initialValue = -2f,
        targetValue = 2f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 350, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "suspension_bounce"
    )

    // Wiper blade sweep angle
    val wiperAngle by infiniteTransition.animateFloat(
        initialValue = -50f,
        targetValue = 50f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1300, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "wiper_angle"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0A0B10))
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 120.dp)
        ) {
            // 1. TOP STATUS BAR (Exact match to busdriver.wtf top bar)
            item(key = "bus_top_status_bar") {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF0F111A))
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Left: Bus Icon + Route tag
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable {
                            selectedRouteIndex = (selectedRouteIndex + 1) % InteractiveTravelData.busRoutes.size
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        }
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFFFB703)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.explore_outlined),
                                contentDescription = null,
                                tint = Color.Black,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = stringResource(R.string.bus_driver_title_hindi),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Black,
                                color = Color.White
                            )
                            Text(
                                text = InteractiveTravelData.busRoutes[selectedRouteIndex],
                                style = MaterialTheme.typography.labelSmall,
                                fontFamily = FontFamily.Monospace,
                                color = Color(0xFFA0AAB8)
                            )
                        }
                    }

                    // Right: Live Clock + Passenger count
                    Column(horizontalAlignment = Alignment.End) {
                        Row(verticalAlignment = Alignment.Bottom) {
                            val parts = currentTimeString.split(" ")
                            Text(
                                text = parts.getOrElse(0) { "23:57" },
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontFamily = FontFamily.Monospace
                            )
                            if (parts.size > 1) {
                                Text(
                                    text = " ${parts[1]}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color(0xFF8A95A5),
                                    fontFamily = FontFamily.Monospace,
                                    modifier = Modifier.padding(bottom = 2.dp)
                                )
                            }
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFFB8500))
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "$passengerCount ABOARD",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp,
                                color = Color(0xFFFB8500)
                            )
                        }
                    }
                }
            }

            // 2. HERO TYPOGRAPHY & SUBTITLE (1:1 Replica)
            item(key = "bus_hero_banner") {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp, bottom = 12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "2 2 7   T R A C K S   •   N O N - S T O P",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF8A95A5),
                        letterSpacing = 1.5.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = stringResource(R.string.bus_driver_title_hindi),
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Black,
                        fontSize = 44.sp,
                        color = Color.White,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "A L L   N I G H T   O N   N H   4 8",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF8A95A5),
                        letterSpacing = 2.sp
                    )
                }
            }

            // 3. INTERACTIVE "HORN OK PLEASEEEE" BUTTON
            item(key = "bus_horn_button") {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 48.dp, vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(60.dp)
                            .clip(RoundedCornerShape(30.dp))
                            .clickable {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                playPneumaticHornSound()
                                hornFlashing = true
                                android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                                    hornFlashing = false
                                }, 650)
                            },
                        color = Color(0xFF1B1F2D),
                        shape = RoundedCornerShape(30.dp),
                        border = BorderStroke(1.5.dp, Color(0xFF2E364A))
                    ) {
                        Row(
                            modifier = Modifier.fillMaxSize(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.radio),
                                contentDescription = null,
                                tint = Color(0xFFFFB703),
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = stringResource(R.string.horn_ok_please_hindi),
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = stringResource(R.string.horn_ok_please_english),
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 9.sp,
                                    color = Color(0xFF8A95A5)
                                )
                            }
                        }
                    }
                }
            }

            // HORN VISUAL FLASH OVERLAY
            if (hornFlashing) {
                item(key = "horn_flash_banner") {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp)
                            .background(Color(0xFFFFB703), RoundedCornerShape(12.dp))
                            .padding(8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "🔊 BEEP BEEP! HORN OK PLEASE! 📢",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Black,
                            color = Color.Black
                        )
                    }
                }
            }

            // 4. MOVING BUS & HIGHWAY CANVAS (Centerpiece)
            item(key = "bus_highway_canvas") {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp)
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .border(1.5.dp, Color(0xFF283144), RoundedCornerShape(20.dp))
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val width = size.width
                        val height = size.height

                        // Night Sky Gradient
                        drawRect(
                            brush = Brush.verticalGradient(
                                colors = listOf(Color(0xFF04060A), Color(0xFF0D121F), Color(0xFF182234))
                            )
                        )

                        // Stars
                        val starList = listOf(
                            Offset(width * 0.12f, height * 0.12f),
                            Offset(width * 0.28f, height * 0.08f),
                            Offset(width * 0.52f, height * 0.18f),
                            Offset(width * 0.76f, height * 0.10f),
                            Offset(width * 0.88f, height * 0.22f)
                        )
                        starList.forEach { pos ->
                            drawCircle(color = Color.White.copy(alpha = 0.85f), radius = 2f, center = pos)
                        }

                        // Desert Horizon Mountains
                        val mountainPath = Path().apply {
                            moveTo(0f, height * 0.52f)
                            lineTo(width * 0.20f, height * 0.44f)
                            lineTo(width * 0.45f, height * 0.50f)
                            lineTo(width * 0.70f, height * 0.42f)
                            lineTo(width * 0.90f, height * 0.48f)
                            lineTo(width, height * 0.52f)
                            lineTo(width, height)
                            lineTo(0f, height)
                            close()
                        }
                        drawPath(path = mountainPath, color = Color(0xFF0A0F19))

                        // Road Surface Polygon
                        val horizonY = height * 0.52f
                        val roadPath = Path().apply {
                            moveTo(width * 0.44f, horizonY)
                            lineTo(width * 0.56f, horizonY)
                            lineTo(width * 0.98f, height)
                            lineTo(width * 0.02f, height)
                            close()
                        }
                        drawPath(path = roadPath, color = Color(0xFF121824))

                        // Moving Yellow Center Dashed Lines
                        val numDashes = 6
                        for (i in 0 until numDashes) {
                            val progress = ((i.toFloat() / numDashes) + roadOffset) % 1f
                            val y = horizonY + (height - horizonY) * progress
                            val dashW = 2f + 16f * progress
                            val dashH = 4f + 26f * progress
                            val centerX = width * 0.5f

                            drawRect(
                                color = Color(0xFFFFB703).copy(alpha = 0.25f + 0.75f * progress),
                                topLeft = Offset(centerX - dashW / 2f, y),
                                size = Size(dashW, dashH)
                            )
                        }

                        // Rain Overlays
                        val rainCount = 45
                        for (r in 0 until rainCount) {
                            val rx = (r * 41 + (roadOffset * 220).toInt()) % width.toInt()
                            val ry = (r * 59 + (roadOffset * 450).toInt()) % height.toInt()
                            drawLine(
                                color = Color(0x66A5D6A7),
                                start = Offset(rx.toFloat(), ry.toFloat()),
                                end = Offset(rx.toFloat() - 5f, ry.toFloat() + 16f),
                                strokeWidth = 1.5f
                            )
                        }

                        // Animated Wiper Blade
                        val wiperOrigin = Offset(width * 0.5f, height)
                        val angleRad = Math.toRadians((wiperAngle - 90).toDouble())
                        val wiperLen = height * 0.72f
                        val wiperEnd = Offset(
                            wiperOrigin.x + (wiperLen * cos(angleRad)).toFloat(),
                            wiperOrigin.y + (wiperLen * sin(angleRad)).toFloat()
                        )
                        drawLine(
                            color = Color(0xCC3E4C63),
                            start = wiperOrigin,
                            end = wiperEnd,
                            strokeWidth = 5f,
                            cap = StrokeCap.Round
                        )
                    }

                    // Iconic Colorful Indian Bus Graphic Illustration (Centerpiece)
                    Box(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .offset(y = suspensionBounce.dp)
                            .width(280.dp)
                            .height(130.dp)
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val w = size.width
                            val h = size.height

                            // 1. Headlight Glow Beams
                            val headlightBeam = Path().apply {
                                moveTo(w * 0.88f, h * 0.65f)
                                lineTo(w, h * 0.45f)
                                lineTo(w, h * 0.90f)
                                close()
                            }
                            drawPath(
                                path = headlightBeam,
                                brush = Brush.horizontalGradient(
                                    colors = listOf(Color(0xFFFFD166).copy(alpha = 0.6f), Color.Transparent)
                                )
                            )

                            // 2. Bus Main Body (Teal / Orange / Yellow Indian Truck Bus)
                            drawRoundRect(
                                color = Color(0xFFFB8500),
                                topLeft = Offset(w * 0.08f, h * 0.25f),
                                size = Size(w * 0.80f, h * 0.52f),
                                cornerRadius = CornerRadius(14f, 14f)
                            )

                            // Bus Lower Stripe Banner
                            drawRect(
                                color = Color(0xFF023E8A),
                                topLeft = Offset(w * 0.08f, h * 0.55f),
                                size = Size(w * 0.80f, h * 0.12f)
                            )

                            // Bus Decorative Art Trim
                            drawRect(
                                color = Color(0xFF2A9D8F),
                                topLeft = Offset(w * 0.08f, h * 0.42f),
                                size = Size(w * 0.80f, h * 0.06f)
                            )

                            // 3. Cabin Windows
                            val windowCount = 5
                            val windowW = w * 0.11f
                            val windowH = h * 0.22f
                            for (wn in 0 until windowCount) {
                                val wx = w * 0.12f + wn * (windowW + w * 0.03f)
                                drawRoundRect(
                                    color = Color(0xFF8ECAE6),
                                    topLeft = Offset(wx, h * 0.30f),
                                    size = Size(windowW, windowH),
                                    cornerRadius = CornerRadius(6f, 6f)
                                )
                            }

                            // Driver Front Windshield
                            drawRoundRect(
                                color = Color(0xFFA8DADC),
                                topLeft = Offset(w * 0.77f, h * 0.30f),
                                size = Size(w * 0.10f, windowH),
                                cornerRadius = CornerRadius(6f, 6f)
                            )

                            // 4. Roof Luggage Rack & Bags
                            drawRect(
                                color = Color(0xFF333333),
                                topLeft = Offset(w * 0.15f, h * 0.16f),
                                size = Size(w * 0.65f, h * 0.09f)
                            )
                            // Trunk Box 1
                            drawRoundRect(
                                color = Color(0xFFE76F51),
                                topLeft = Offset(w * 0.20f, h * 0.07f),
                                size = Size(w * 0.16f, h * 0.10f),
                                cornerRadius = CornerRadius(4f, 4f)
                            )
                            // Sleeping Bag 2
                            drawRoundRect(
                                color = Color(0xFF2A9D8F),
                                topLeft = Offset(w * 0.42f, h * 0.08f),
                                size = Size(w * 0.18f, h * 0.09f),
                                cornerRadius = CornerRadius(6f, 6f)
                            )

                            // 5. Animated Rotating Wheels (Front & Rear)
                            val rearWheelCenter = Offset(w * 0.28f, h * 0.78f)
                            val frontWheelCenter = Offset(w * 0.72f, h * 0.78f)
                            val wheelRadius = h * 0.16f

                            listOf(rearWheelCenter, frontWheelCenter).forEach { center ->
                                drawCircle(color = Color(0xFF111111), radius = wheelRadius, center = center)
                                drawCircle(color = Color(0xFF666666), radius = wheelRadius * 0.55f, center = center)

                                // Rotating Wheel Spokes
                                val rad = Math.toRadians(wheelRotation.toDouble())
                                for (sp in 0..3) {
                                    val angle = rad + sp * Math.PI / 2.0
                                    val spEnd = Offset(
                                        center.x + (wheelRadius * 0.5f * cos(angle)).toFloat(),
                                        center.y + (wheelRadius * 0.5f * sin(angle)).toFloat()
                                    )
                                    drawLine(
                                        color = Color.White,
                                        start = center,
                                        end = spEnd,
                                        strokeWidth = 2.5f
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // 5. LANGUAGE & CATEGORY FILTERS (Chips)
            item(key = "bus_filters") {
                Column(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                    // Category Chips
                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val categoryChips = listOf("90s", "NOSTALGIC", "15 AUG", "BHOJPURI", "BANGLA / বাংলা")
                        itemsIndexed(categoryChips) { _, chipText ->
                            val isSelected = selectedCategoryChip == chipText
                            Surface(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(20.dp))
                                    .clickable { selectedCategoryChip = chipText },
                                color = if (isSelected) Color(0xFFFFB703) else Color(0xFF1B1F2D),
                                shape = RoundedCornerShape(20.dp),
                                border = BorderStroke(1.dp, if (isSelected) Color(0xFFFFB703) else Color(0xFF2E364A))
                            ) {
                                Text(
                                    text = chipText,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) Color.Black else Color.White,
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Language Selectors
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val langs = listOf("EN", "हिं", "मरा", "বাংলা")
                        langs.forEach { lang ->
                            val isSelected = selectedLanguage == lang
                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .clickable { selectedLanguage = lang },
                                color = if (isSelected) Color(0xFF2196F3) else Color(0xFF161A26),
                                shape = RoundedCornerShape(10.dp),
                                border = BorderStroke(1.dp, if (isSelected) Color(0xFF2196F3) else Color(0xFF2A3245))
                            ) {
                                Text(
                                    text = lang,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center,
                                    color = if (isSelected) Color.White else Color(0xFFA0AAB8),
                                    modifier = Modifier.padding(vertical = 8.dp)
                                )
                            }
                        }
                    }
                }
            }

            // 6. CURATED PLAYLIST SELECTION ROW
            item(key = "bus_playlist_selector") {
                Column(modifier = Modifier.padding(top = 12.dp)) {
                    Text(
                        text = "BUS DRIVER PLAYLISTS",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFA0AAB8),
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                    )

                    LazyRow(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        itemsIndexed(InteractiveTravelData.busDriverPlaylists) { idx, playlist ->
                            val isSelected = idx == selectedPlaylistIndex
                            Card(
                                onClick = { selectedPlaylistIndex = idx },
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isSelected) Color(0xFFFB8500) else Color(0xFF1B1F2D)
                                ),
                                modifier = Modifier.width(220.dp)
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text(
                                        text = playlist.categoryBadge,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Black,
                                        color = if (isSelected) Color.Black else Color(0xFFFFB703)
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = playlist.title,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        text = "${playlist.tracks.size} Tracks",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Color.White.copy(alpha = 0.8f)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // 7. TRACKLIST ITEMS
            item(key = "bus_tracks_header") {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "CURATED 90s TRACKS",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFA0AAB8)
                    )
                    Text(
                        text = "VIEW ALL",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFFFB703),
                        modifier = Modifier.clickable { showPlaylistSheet = true }
                    )
                }
            }

            itemsIndexed(selectedPlaylist.tracks, key = { _, trk -> trk.id }) { idx, track ->
                val isCurrent = currentMediaId == track.id
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isCurrent) Color(0xFF283248) else Color(0xFF161A26)
                    ),
                    onClick = { onPlayPlaylistTrack(selectedPlaylist, idx) }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(46.dp)
                                .clip(RoundedCornerShape(10.dp))
                        ) {
                            AsyncImage(
                                model = track.thumbnailUrl,
                                contentDescription = track.title,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                            if (isCurrent) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(Color.Black.copy(alpha = 0.5f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        painter = painterResource(if (isPlaying) R.drawable.pause else R.drawable.play),
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = track.title,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.SemiBold,
                                color = if (isCurrent) Color(0xFFFFB703) else Color.White,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = "${track.artist} • ${track.tag}",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFFA0AAB8),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        Icon(
                            painter = painterResource(R.drawable.play),
                            contentDescription = null,
                            tint = Color(0xFFFFB703),
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }
            }
        }

        // 8. PERSISTENT FLOATING BOTTOM PLAYER DOCK (Exact match to busdriver.wtf screenshot!)
        BusDriverBottomPlayerDock(
            currentMediaId = currentMediaId,
            isPlaying = isPlaying,
            selectedPlaylist = selectedPlaylist,
            onOpenSheet = { showPlaylistSheet = true },
            onPlayPauseToggle = {
                if (selectedPlaylist.tracks.isNotEmpty()) {
                    onPlayPlaylistTrack(selectedPlaylist, 0)
                }
            }
        )

        // EXPANDABLE FULL PLAYLIST BOTTOM SHEET
        if (showPlaylistSheet) {
            ModalBottomSheet(
                onDismissRequest = { showPlaylistSheet = false },
                sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
                containerColor = Color(0xFF0F111A)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "227 TRACKS • NON-STOP 90s HIGHWAY",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFFFFB703)
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    LazyColumn(modifier = Modifier.height(400.dp)) {
                        itemsIndexed(selectedPlaylist.tracks) { idx, track ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .clickable {
                                        onPlayPlaylistTrack(selectedPlaylist, idx)
                                        showPlaylistSheet = false
                                    }
                                    .padding(vertical = 10.dp, horizontal = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "${idx + 1}",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFA0AAB8),
                                    modifier = Modifier.width(30.dp)
                                )
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = track.title,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                    Text(
                                        text = track.artist,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Color(0xFFA0AAB8)
                                    )
                                }
                                Icon(
                                    painter = painterResource(R.drawable.play),
                                    contentDescription = null,
                                    tint = Color(0xFFFFB703),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BusDriverBottomPlayerDock(
    currentMediaId: String?,
    isPlaying: Boolean,
    selectedPlaylist: InteractivePlaylist,
    onOpenSheet: () -> Unit,
    onPlayPauseToggle: () -> Unit
) {
    val currentTrack = selectedPlaylist.tracks.find { it.id == currentMediaId } ?: selectedPlaylist.tracks.firstOrNull()

    val infiniteTransition = rememberInfiniteTransition(label = "dock_cat_anim")
    val catBounce by infiniteTransition.animateFloat(
        initialValue = -3f,
        targetValue = 3f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 450, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "cat_bounce"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(12.dp, RoundedCornerShape(24.dp)),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF161313))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Album Artwork with vinyl effect / dancing cat icon
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF261D1D)),
                        contentAlignment = Alignment.Center
                    ) {
                        AsyncImage(
                            model = currentTrack?.thumbnailUrl ?: selectedPlaylist.coverUrl,
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                        if (isPlaying) {
                            // Dancing Cat avatar overlay matching screenshot
                            Text(
                                text = "🐱",
                                fontSize = 22.sp,
                                modifier = Modifier.offset(y = catBounce.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    // Track Title & Artist
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = currentTrack?.title ?: "Humko Tumse Pyar Hai",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = currentTrack?.artist ?: "SonyMusicIndiaVEVO",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFFA0AAB8),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Progress seekbar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("0:19", style = MaterialTheme.typography.labelSmall, color = Color(0xFFA0AAB8), fontSize = 10.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(4.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(Color(0xFF382B2B))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .fillMaxWidth(0.25f)
                                .background(Color(0xFFFB8500))
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("5:02", style = MaterialTheme.typography.labelSmall, color = Color(0xFFA0AAB8), fontSize = 10.sp)
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Controls row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = {}) {
                        Icon(painter = painterResource(R.drawable.shuffle), contentDescription = null, tint = Color(0xFFA0AAB8), modifier = Modifier.size(20.dp))
                    }
                    IconButton(onClick = {}) {
                        Icon(painter = painterResource(R.drawable.skip_previous), contentDescription = null, tint = Color.White, modifier = Modifier.size(24.dp))
                    }

                    // Play/Pause Big Center Button
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF382B2B))
                            .clickable { onPlayPauseToggle() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(if (isPlaying) R.drawable.pause else R.drawable.play),
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(26.dp)
                        )
                    }

                    IconButton(onClick = {}) {
                        Icon(painter = painterResource(R.drawable.skip_next), contentDescription = null, tint = Color.White, modifier = Modifier.size(24.dp))
                    }
                    IconButton(onClick = { onOpenSheet() }) {
                        Icon(painter = painterResource(R.drawable.queue_music), contentDescription = null, tint = Color(0xFFFFB703), modifier = Modifier.size(22.dp))
                    }
                }
            }
        }
    }
}

// ============================================================================
// TAB 2: DELUXE SALOON (1:1 PIXEL PERFECT REPLICA OF saloon.wtf)
// ============================================================================

@Composable
fun DeluxeSaloonInteractiveTransistor(
    currentMediaId: String?,
    isPlaying: Boolean,
    onPlayPlaylistTrack: (InteractivePlaylist, Int) -> Unit
) {
    var selectedStationIndex by remember { mutableIntStateOf(0) }
    var currentQuoteIndex by remember { mutableIntStateOf(0) }
    val selectedStation = InteractiveTravelData.deluxeSaloonPlaylists[selectedStationIndex]

    // Ambience layer toggles & sliders
    var scissorsFxEnabled by remember { mutableStateOf(true) }
    var scissorsVolume by remember { mutableFloatStateOf(0.7f) }
    var ceilingFanEnabled by remember { mutableStateOf(true) }
    var vinylStaticEnabled by remember { mutableStateOf(true) }

    // Live clock & Online counter
    var currentTimeString by remember { mutableStateOf("7:49 AM") }
    var onlineCounter by remember { mutableIntStateOf(1141) }

    val haptic = LocalHapticFeedback.current

    LaunchedEffect(Unit) {
        val formatter = SimpleDateFormat("h:mm a", Locale.getDefault())
        while (true) {
            currentTimeString = formatter.format(Date()).uppercase()
            if (Random.nextInt(0, 10) > 6) {
                onlineCounter = (onlineCounter + Random.nextInt(-3, 4)).coerceIn(1000, 1500)
            }
            delay(2000L)
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "saloon_anim")

    // Ceiling Fan Blade Rotation
    val fanRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = if (ceilingFanEnabled) 1200 else 10000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "fan_rotation"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0F0B09))
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 140.dp)
        ) {
            // 1. TOP STATUS BAR (Clock + 1141 online match to saloon.wtf)
            item(key = "saloon_top_bar") {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Left: Time pill
                    Surface(
                        color = Color(0xFF1E1410),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text(
                            text = currentTimeString,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                        )
                    }

                    // Right: Live Online User Count pill
                    Surface(
                        color = Color(0xFF1E1410),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF2A9D8F))
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "$onlineCounter online",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            }

            // 2. RETRO INDIAN BARBERSHOP SCENE & DEVANAGARI BANNER
            item(key = "saloon_hero_poster") {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(340.dp)
                        .padding(horizontal = 16.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .border(1.dp, Color(0xFF3E281C), RoundedCornerShape(24.dp))
                ) {
                    // Vintage Barbershop Photo Backdrop
                    AsyncImage(
                        model = selectedStation.coverUrl,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )

                    // Dark gradient overlay
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Black.copy(alpha = 0.4f),
                                        Color.Black.copy(alpha = 0.85f)
                                    )
                                )
                            )
                    )

                    // Hero Devanagari Banner: Dealux Saloon
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.TopCenter)
                            .padding(top = 24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = stringResource(R.string.deluxe_saloon_title_hindi),
                            style = MaterialTheme.typography.headlineLarge,
                            fontWeight = FontWeight.Black,
                            fontSize = 42.sp,
                            color = Color(0xFFFAEDCD),
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "POWERED BY ",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFD4A373),
                                letterSpacing = 2.sp
                            )
                            Text(
                                text = "RJ MEDIA",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Black,
                                color = Color.White,
                                letterSpacing = 2.sp
                            )
                        }
                    }

                    // Interactive Barber Quote Card Overlay (Exact match to saloon.wtf image!)
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.BottomCenter)
                            .padding(16.dp)
                            .clickable {
                                currentQuoteIndex = (currentQuoteIndex + 1) % InteractiveTravelData.saloonQuotes.size
                                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                            },
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xDD1E1410))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = InteractiveTravelData.saloonQuotes[currentQuoteIndex],
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFFFAEDCD),
                                textAlign = TextAlign.Center,
                                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                            )
                        }
                    }
                }
            }

            // 3. ANALOG ROTARY FREQUENCY TUNING DIAL (TRANSISTOR RADIO)
            item(key = "saloon_transistor_dial") {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF231610))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "ANALOG FREQUENCY TUNER",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFFD4A373)
                            )
                            Text(
                                text = selectedStation.frequencyMHz,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFFE76F51)
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Tuner Scale Canvas
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            color = Color(0xFF120B07),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Box(modifier = Modifier.fillMaxSize()) {
                                Canvas(modifier = Modifier.fillMaxSize()) {
                                    val width = size.width
                                    val height = size.height

                                    val numTicks = 32
                                    for (i in 0..numTicks) {
                                        val x = (width / numTicks) * i
                                        val isMajor = i % 4 == 0
                                        val tickHeight = if (isMajor) height * 0.50f else height * 0.28f
                                        drawLine(
                                            color = if (isMajor) Color(0xFFD4A373) else Color(0xFF5E4332),
                                            start = Offset(x, 0f),
                                            end = Offset(x, tickHeight),
                                            strokeWidth = if (isMajor) 2f else 1f
                                        )
                                    }

                                    // Tuning Needle Indicator
                                    val needleX = width * (0.30f + selectedStationIndex * 0.40f)
                                    drawLine(
                                        color = Color(0xFFE76F51),
                                        start = Offset(needleX, 0f),
                                        end = Offset(needleX, height),
                                        strokeWidth = 4f
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Radio Station Preset Buttons
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            InteractiveTravelData.deluxeSaloonPlaylists.forEachIndexed { index, station ->
                                val isSelected = index == selectedStationIndex
                                Button(
                                    onClick = {
                                        selectedStationIndex = index
                                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                    },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (isSelected) Color(0xFFE76F51) else Color(0xFF382318)
                                    )
                                ) {
                                    Text(
                                        text = station.frequencyMHz,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // 4. AMBIENCE MIXER LAYER (Scissor Snips, Ceiling Fan, Radio Crackle)
            item(key = "saloon_ambience_mixer") {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF231610))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "SALOON AMBIENCE MIXER",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFFD4A373)
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        // Scissor Snips FX
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.clickable {
                                    if (scissorsFxEnabled) {
                                        playScissorSnipSound()
                                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                    }
                                }
                            ) {
                                Text("✂️ Barber Scissors Snip", style = MaterialTheme.typography.bodyMedium, color = Color.White)
                            }
                            Switch(
                                checked = scissorsFxEnabled,
                                onCheckedChange = { scissorsFxEnabled = it },
                                colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFFE76F51))
                            )
                        }

                        // Ceiling Fan Drone
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    painter = painterResource(R.drawable.radio),
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(18.dp)
                                        .graphicsLayer(rotationZ = fanRotation),
                                    tint = Color(0xFF2A9D8F)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("🌀 Ceiling Fan Drone", style = MaterialTheme.typography.bodyMedium, color = Color.White)
                            }
                            Switch(
                                checked = ceilingFanEnabled,
                                onCheckedChange = { ceilingFanEnabled = it },
                                colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFF2A9D8F))
                            )
                        }
                    }
                }
            }

            // 5. SALOON TRACKS LIST
            item(key = "saloon_tracks_header") {
                Text(
                    text = "VINTAGE SALOON PLAYLIST",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFD4A373),
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }

            itemsIndexed(selectedStation.tracks, key = { _, trk -> trk.id }) { idx, track ->
                val isCurrent = currentMediaId == track.id
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isCurrent) Color(0xFF3D251A) else Color(0xFF1E130D)
                    ),
                    onClick = { onPlayPlaylistTrack(selectedStation, idx) }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(RoundedCornerShape(10.dp))
                        ) {
                            AsyncImage(
                                model = track.thumbnailUrl,
                                contentDescription = track.title,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                            if (isCurrent) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(Color.Black.copy(alpha = 0.5f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        painter = painterResource(if (isPlaying) R.drawable.pause else R.drawable.play),
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = track.title,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.SemiBold,
                                color = if (isCurrent) Color(0xFFFFB703) else Color.White,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = "${track.artist} • ${track.tag}",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFFD4A373),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        Icon(
                            painter = painterResource(R.drawable.play),
                            contentDescription = null,
                            tint = Color(0xFFE76F51),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }

        // 6. FLOATING SALOON PLAYER DOCK (Exact match to saloon.wtf screenshot!)
        SaloonBottomPlayerDock(
            currentMediaId = currentMediaId,
            isPlaying = isPlaying,
            selectedStation = selectedStation,
            onPlayPauseToggle = {
                if (selectedStation.tracks.isNotEmpty()) {
                    onPlayPlaylistTrack(selectedStation, 0)
                }
            }
        )
    }
}

@Composable
fun SaloonBottomPlayerDock(
    currentMediaId: String?,
    isPlaying: Boolean,
    selectedStation: InteractivePlaylist,
    onPlayPauseToggle: () -> Unit
) {
    val currentTrack = selectedStation.tracks.find { it.id == currentMediaId } ?: selectedStation.tracks.firstOrNull()

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(16.dp, RoundedCornerShape(28.dp)),
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1512))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Album Artwork Square Thumbnail (Match screenshot 1)
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(RoundedCornerShape(12.dp))
                    ) {
                        AsyncImage(
                            model = currentTrack?.thumbnailUrl ?: selectedStation.coverUrl,
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    // Track Title & Subtext
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = currentTrack?.title ?: "O yaaron maaf karn...",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = currentTrack?.artist ?: "Purane Yaadien wo • 2000s Ke Haircut",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFFD4A373),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    // Prev Button
                    IconButton(onClick = {}) {
                        Icon(
                            painter = painterResource(R.drawable.skip_previous),
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    // Big Yellow Circular Play/Pause Button (Match screenshot 1!)
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFFFB703))
                            .clickable { onPlayPauseToggle() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(if (isPlaying) R.drawable.pause else R.drawable.play),
                            contentDescription = null,
                            tint = Color.Black,
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    // Next Button
                    IconButton(onClick = {}) {
                        Icon(
                            painter = painterResource(R.drawable.skip_next),
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Progress seekbar (0:03 --------- 4:12)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("0:03", style = MaterialTheme.typography.labelSmall, color = Color(0xFFD4A373), fontSize = 11.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(4.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(Color(0xFF3E281C))
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .fillMaxWidth(0.12f)
                                .background(Color(0xFFFFB703))
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("4:12", style = MaterialTheme.typography.labelSmall, color = Color(0xFFD4A373), fontSize = 11.sp)
                }
            }
        }
    }
}
