/**
 * Metrolist Project (C) 2026
 * Licensed under GPL-3.0 | See git history for contributors
 */

package com.metrolist.music.ui.screens

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.media3.common.MediaItem
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.metrolist.innertube.YouTube
import com.metrolist.innertube.models.SongItem
import com.metrolist.music.LocalPlayerConnection
import com.metrolist.music.R
import com.metrolist.music.extensions.toMediaItem
import com.metrolist.music.models.MediaMetadata
import com.metrolist.music.models.toMediaMetadata
import com.metrolist.music.playback.queues.ListQueue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.util.Calendar
import java.util.Locale

data class PujaTrack(
    val title: String,
    val artist: String,
    val fallbackId: String? = null,
    val formattedDuration: String = "4:00"
)

val durgaPujaTracks = listOf(
    PujaTrack("Purano Sei Diner Kotha", "Hemanta Mukherjee", "Purano_Sei_Diner_Kotha"),
    PujaTrack("Coffee Houser Sei Addata", "Manna Dey", "Coffee_Houser_Sei_Addata"),
    PujaTrack("Ei Poth Jodi Na Shesh Hoy", "Hemanta & Sandhya Mukherjee", "Ei_Poth_Jodi_Na_Shesh_Hoy"),
    PujaTrack("Jibane Ki Pabo Na", "Manna Dey", "Jibane_Ki_Pabo_Na"),
    PujaTrack("Priyotoma Mone Rekho", "Kumar Sanu", "Priyotoma_Mone_Rekho"),
    PujaTrack("Ektuku Chhoya Lage", "Kishore Kumar", "Ektuku_Chhoya_Lage"),
    PujaTrack("Gold Printer Sari Pore", "Mita Chatterjee", "Gold_Printer_Sari_Pore"),
    PujaTrack("Amay Ektu Jayga Dao", "Manna Dey", "Amay_Ektu_Jayga_Dao"),
    PujaTrack("Ogo Nirupama Korio Khoma", "Kishore Kumar", "Ogo_Nirupama_Korio_Khoma"),
    PujaTrack("Na Jeyo Na Rajani Ekhano", "Lata Mangeshkar", "Na_Jeyo_Na_Rajani_Ekhano"),
    PujaTrack("Tumi Je Amar", "Sandhya Mukherjee", "Tumi_Je_Amar"),
    PujaTrack("Amay Proshno Kore Nil Dhrubo Tara", "Hemanta Mukherjee", "Amay_Proshno_Kore_Nil_Dhrubo_Tara"),
    PujaTrack("Amar Duarkhani Batas Ese", "Agni Sanskar", "Amar_Duarkhani_Batas_Ese"),
    PujaTrack("Ei Sundar Raatri", "Agni Sanskar", "Ei_Sundar_Raatri"),
    PujaTrack("Ekti Sukher Neer", "Agni Sanskar", "Ekti_Sukher_Neer"),
    PujaTrack("Ankhi Jage Shyam Rup Lage", "Sesh Anka", "Ankhi_Jage_Shyam_Rup_Lage"),
    PujaTrack("Ami to Jani", "Sesh Anka", "Ami_to_Jani"),
    PujaTrack("Ami Cheye Cheye Dekhi", "Deya Neya", "Ami_Cheye_Cheye_Dekhi"),
    PujaTrack("Dole Dodul Dole Jhulana", "Deya Neya", "Dole_Dodul_Dole_Jhulana"),
    PujaTrack("E Gaane Prajapati", "Deya Neya", "E_Gaane_Prajapati"),
    PujaTrack("Gaane Bhuban Bhoriye Debe", "Deya Neya", "Gaane_Bhuban_Bhoriye_Debe"),
    PujaTrack("Jiban Khatar Prati Patay", "Deya Neya", "Jiban_Khatar_Prati_Patay"),
    PujaTrack("Jakhon Bhanglo Milan Mela", "Barnali", "Jakhon_Bhanglo_Milan_Mela"),
    PujaTrack("Aaja Piya", "Alor Pipasa", "Aaja_Piya"),
    PujaTrack("Minati Mor Tomar Paaye", "Alor Pipasa", "Minati_Mor_Tomar_Paaye"),
    PujaTrack("O Radhe Thamke Geli Keno", "Baghini", "O_Radhe_Thamke_Geli_Keno"),
    PujaTrack("Jakhan Daaklo Banshi", "Baghini", "Jakhan_Daaklo_Banshi"),
    PujaTrack("Bojhona Keno Je Tumi", "Adwitiya", "Bojhona_Keno_Je_Tumi"),
    PujaTrack("Chanchal Mon Anmona Hoy", "Adwitiya", "Chanchal_Mon_Anmona_Hoy"),
    PujaTrack("Jabar Bela Pichhu Theke", "Adwitiya", "Jabar_Bela_Pichhu_Theke"),
    PujaTrack("Moner Manush Khunjte", "Adwitiya", "Moner_Manush_Khunjte"),
    PujaTrack("Aalo Amar Aalo", "Aponjon", "Aalo_Amar_Aalo"),
    PujaTrack("Keno Bon Koyela Dake", "Arogyaniketan", "Keno_Bon_Koyela_Dake"),
    PujaTrack("Jodi Basar Pradipe", "Saharer Itikatha", "Jodi_Basar_Pradipe"),
    PujaTrack("Aha Re Bidhi Tor", "Palatak", "Aha_Re_Bidhi_Tor"),
    PujaTrack("Banshi Bujhi Sei Surey", "Sathi Hara", "Banshi_Bujhi_Sei_Surey"),
    PujaTrack("Klantir Path Bujhiba Phuralo", "Bipasha", "Klantir_Path_Bujhiba_Phuralo"),
    PujaTrack("Emon Ami Ghar Bendhechhi", "Natun Jiban", "Emon_Ami_Ghar_Bendhechhi"),
    PujaTrack("Ami Tomare Bhalobesechhi", "Natun Jiban", "Ami_Tomare_Bhalobesechhi"),
    PujaTrack("Khub Hoyechhe Borai Jadu", "Palatak", "Khub_Hoyechhe_Borai_Jadu"),
    PujaTrack("Chinite Parini Bondhu", "Palatak", "Chinite_Parini_Bondhu"),
    PujaTrack("Ogo Bondhu Amar", "Ajana Sapath", "Ogo_Bondhu_Amar"),
    PujaTrack("Dure Keno Elena", "Sathi Hara", "Dure_Keno_Elena"),
    PujaTrack("Aajo Hriday Amar", "Baluchari", "Aajo_Hriday_Amar"),
    PujaTrack("Ami Tomay Khuji Mori", "Basanta Bahar", "Ami_Tomay_Khuji_Mori"),
    PujaTrack("Duti Ankhitara", "Garh Nasimpur", "Duti_Ankhitara"),
    PujaTrack("Je Golap Kanta Ghaaye", "Garh Nasimpur", "Je_Golap_Kanta_Ghaaye"),
    PujaTrack("Bnadho Jhulana", "Basanta Bahar", "Bnadho_Jhulana"),
    PujaTrack("Nachre Bandar Nach", "Sathi Hara", "Nachre_Bandar_Nach"),
    PujaTrack("O Bhai Re Bhai", "Marjinna Abdulla", "O_Bhai_Re_Bhai"),
    PujaTrack("Na Bajaiho Shyam Bairi Bansuri", "Alor Pipasa", "Na_Bajaiho_Shyam_Bairi_Bansuri"),
    PujaTrack("Abhimani Holo Aaj", "Angeekar", "Abhimani_Holo_Aaj"),
    PujaTrack("Piya Bin Nishidin", "Monihar", "Piya_Bin_Nishidin"),
    PujaTrack("Aar Kato Raat Eka Thakbo", "Asha Bhosle", "Aar_Kato_Raat_Eka_Thakbo"),
    PujaTrack("Chirodini Tumi Je Aamar", "Kishore Kumar", "Chirodini_Tumi_Je_Aamar"),
    PujaTrack("Mohuay Jomechhe Aaj Mou Go", "Asha Bhosle", "Mohuay_Jomechhe_Aaj_Mou_Go"),
    PujaTrack("Koto Je Sagor Nodi", "Kumar Sanu", "Koto_Je_Sagor_Nodi"),
    PujaTrack("Amar Shilpi Tumi", "Kumar Sanu", "Amar_Shilpi_Tumi"),
    PujaTrack("Keno Tumi Amake Je Eto Bhalobaso", "Kumar Sanu", "Keno_Tumi_Amake_Je_Eto_Bhalobaso"),
    PujaTrack("Chokhe Chokhe Katha Balo", "Asha Bhosle", "Chokhe_Chokhe_Katha_Balo"),
    PujaTrack("Besh Korechhi Prem Korechhi", "Asha Bhosle", "Besh_Korechhi_Prem_Korechhi"),
    PujaTrack("Ami Dur Hote Tomarei Dekhechhi", "Hemanta Mukherjee", "Ami_Dur_Hote_Tomarei_Dekhechhi"),
    PujaTrack("Neel Akasher Niche Prithibi", "Hemanta Mukherjee", "Neel_Akasher_Niche_Prithibi"),
    PujaTrack("Palki Te Bou Chole Jai", "Mita Chatterjee", "Palki_Te_Bou_Chole_Jai"),
    PujaTrack("Jani Na Kothay Tumi", "Asha Bhosle & R.D. Burman", "Jani_Na_Kothay_Tumi"),
    PujaTrack("Aaj Dole Mon Kar Isharate", "Asha Bhosle & R.D. Burman", "Aaj_Dole_Mon_Kar_Isharate"),
    PujaTrack("Ke Prothom Kachhe Esechhi", "Manna Dey & Lata Mangeshkar", "Ke_Prothom_Kachhe_Esechhi"),
    PujaTrack("Koto Din Dekhini Tomay", "Manna Dey", "Koto_Din_Dekhini_Tomay"),
    PujaTrack("Amar Swapna Tumi Ogo", "Kishore Kumar & Asha Bhosle", "Amar_Swapna_Tumi_Ogo"),
    PujaTrack("Tumi Achho Eto Kachhe Tai", "Kumar Sanu", "Tumi_Achho_Eto_Kachhe_Tai"),
    PujaTrack("Sudhu Tumi Ele Na", "Cactus", "Sudhu_Tumi_Ele_Na"),
    PujaTrack("Aar Kotokal Ami Soibo", "Kumar Sanu", "Aar_Kotokal_Ami_Soibo"),
    PujaTrack("Dugga Elo", "Monali Thakur", "V9n8kC78v-U", "2:27"),
    PujaTrack("Dugga Ma", "Arijit Singh", "0y4s32uKqXw", "4:31"),
    PujaTrack("Ebar Jeno Onno Rokom Pujo", "Nakash Aziz", "xGg70O9tU7w", "3:33"),
    PujaTrack("Dhak Baja Kashor Baja", "Shreya Ghoshal", "v2W0cM2C-dE", "4:26"),
    PujaTrack("Bolo Dugga Elo", "Kaushik-Guddu", "bC1M2dG-M5g", "3:20"),
    PujaTrack("Aamaar Dugga", "Monali Thakur", "L2b-Z3s0tVk", "3:20"),
    PujaTrack("Dhaker Taley", "Release", "c5uP8s7V1tM", "4:43"),
    PujaTrack("Abar Elo Maa", "Rahul Dutta", "Abar_Elo_Maa"),
    PujaTrack("Joy Joy Durga Ma", "Agnibha Bandyopadhyay", "Joy_Joy_Durga_Ma"),
    PujaTrack("Durga Maa", "Akassh", "Durga_Maa"),
    PujaTrack("Gouri Elo Dekhe Jalo", "DOHAR FOLK", "Gouri_Elo_Dekhe_Jalo"),
    PujaTrack("Dhak Baaja Komor Nacha", "Release", "Dhak_Baaja_Komor_Nacha"),
    PujaTrack("Durge Durge Durgatinashini", "Asha Bhosle", "Durge_Durge_Durgatinashini"),
    PujaTrack("Rupang Dehi", "Snita Pramanik Ghosh", "Rupang_Dehi"),
    PujaTrack("Jago Uma", "Rupankar", "Jago_Uma"),
    PujaTrack("Aigiri Nandini", "Sowrabha", "Aigiri_Nandini"),
    PujaTrack("Ailo Uma Barite", "Antara Nandy", "Ailo_Uma_Barite"),
    PujaTrack("Aaj Baaje", "Somchanda Bhattacharya", "Aaj_Baaje"),
    PujaTrack("Yoddhar Saathe Ebar Pujo Katan", "Nakash Aziz", "Yoddhar_Saathe_Ebar_Pujo_Katan"),
    PujaTrack("Pujo Pujo Gondho", "Anupam Roy", "Pujo_Pujo_Gondho"),
    PujaTrack("Pujor Dhaak Theme", "Surobaibhab (Bibhabendu Bhattacharya)", "Pujor_Dhaak_Theme")
)

val mahalayaTracks = listOf(
    PujaTrack("Mahalaya", "Birendra Krishna Bhadra", "gU1nO_w9f_M", "1:28:45")
)

val mahalayaSongsTracks = listOf(
    PujaTrack("Ya Chandi", "Mahalaya", "Ya_Chandi"),
    PujaTrack("Simhastha Sashisekhara", "Mahalaya", "Simhastha_Sashisekhara"),
    PujaTrack("Bajlo Tomar Aalor Benu", "Mahalaya", "Bajlo_Tomar_Aalor_Benu"),
    PujaTrack("Jago Durga Dashapraharanadharini", "Mahalaya", "Jago_Durga_Dashapraharanadharini"),
    PujaTrack("Ogo Amar Agamani-alo", "Mahalaya", "Ogo_Amar_Agamani_alo"),
    PujaTrack("Tabo Achintya Rupa-charita", "Mahalaya", "Tabo_Achintya_Rupa_charita"),
    PujaTrack("Aham Rudrebhirvasubhischara", "Mahalaya", "Aham_Rudrebhirvasubhischara"),
    PujaTrack("Jayanti Mangala Kali", "Mahalaya", "Jayanti_Mangala_Kali"),
    PujaTrack("Subhra Sankha-rabe", "Mahalaya", "Subhra_Sankha_rabe"),
    PujaTrack("Jatajutasamayuktamardhendu", "Mahalaya", "Jatajutasamayuktamardhendu"),
    PujaTrack("Namo Chandi, Namo Chandi", "Mahalaya", "Namo_Chandi_Namo_Chandi"),
    PujaTrack("Ma Go Tabu Beene Sangeeta", "Mahalaya", "Ma_Go_Tabu_Beene_Sangeeta"),
    PujaTrack("Bimane Bimane", "Mahalaya", "Bimane_Bimane"),
    PujaTrack("Jaya Jaya Japyajaye", "Mahalaya", "Jaya_Jaya_Japyajaye"),
    PujaTrack("He Chinmoyi", "Mahalaya", "He_Chinmoyi"),
    PujaTrack("Amala-kirane Tribhubana-monohari", "Mahalaya", "Amala_kirane_Tribhubana_monohari"),
    PujaTrack("Jayanti Mangala Kali (Pankaj Kumar Mullick)", "Mahalaya", "Jayanti_Mangala_Kali_Pankaj"),
    PujaTrack("Santi Dile Bhari", "Mahalaya", "Santi_Dile_Bhari")
)

private val resolvedTrackCache = java.util.concurrent.ConcurrentHashMap<String, MediaItem>()

suspend fun resolvePujaTrackToMediaItem(track: PujaTrack): MediaItem = withContext(Dispatchers.IO) {
    val cacheKey = "${track.title} ${track.artist}"
    resolvedTrackCache[cacheKey]?.let { return@withContext it }

    if (!track.fallbackId.isNullOrEmpty() && track.fallbackId.length == 11) {
        val metadata = MediaMetadata(
            id = track.fallbackId,
            title = track.title,
            artists = listOf(MediaMetadata.Artist(id = null, name = track.artist)),
            duration = 240,
            thumbnailUrl = "https://i.ytimg.com/vi/${track.fallbackId}/hqdefault.jpg"
        )
        val mediaItem = metadata.toMediaItem()
        resolvedTrackCache[cacheKey] = mediaItem
        return@withContext mediaItem
    }
    try {
        val query = "${track.title} ${track.artist}"
        val searchResult = YouTube.search(query, YouTube.SearchFilter.FILTER_SONG).getOrNull()
        val songItem = searchResult?.items?.filterIsInstance<SongItem>()?.firstOrNull()
        if (songItem != null) {
            val mediaItem = songItem.toMediaMetadata().toMediaItem()
            resolvedTrackCache[cacheKey] = mediaItem
            return@withContext mediaItem
        }
    } catch (e: Exception) {
        Timber.e(e, "Error resolving YouTube stream for track ${track.title}")
    }
    val fallbackVideoId = track.fallbackId?.takeIf { it.length == 11 } ?: "V9n8kC78v-U"
    val metadata = MediaMetadata(
        id = fallbackVideoId,
        title = track.title,
        artists = listOf(MediaMetadata.Artist(id = null, name = track.artist)),
        duration = 240,
        thumbnailUrl = "https://i.ytimg.com/vi/$fallbackVideoId/hqdefault.jpg"
    )
    val mediaItem = metadata.toMediaItem()
    resolvedTrackCache[cacheKey] = mediaItem
    return@withContext mediaItem
}

private fun formatTime(millis: Long): String {
    if (millis <= 0) return "0:00"
    val totalSeconds = millis / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return String.format(Locale.getDefault(), "%d:%02d", minutes, seconds)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DurgaPujaScreen(
    navController: NavController
) {
    val playerConnection = LocalPlayerConnection.current
    val coroutineScope = rememberCoroutineScope()

    val isPlayingState = playerConnection?.isPlaying?.collectAsState()
    val isPlaying = isPlayingState?.value == true

    val mediaMetadataState = playerConnection?.mediaMetadata?.collectAsState()
    val currentMetadata = mediaMetadataState?.value

    var currentPosition by remember { mutableLongStateOf(0L) }
    var totalDuration by remember { mutableLongStateOf(0L) }
    var resolvingIndex by remember { mutableIntStateOf(-1) }

    LaunchedEffect(isPlaying, currentMetadata) {
        while (true) {
            val player = playerConnection?.player
            if (player != null) {
                currentPosition = player.currentPosition.coerceAtLeast(0L)
                totalDuration = player.duration.coerceAtLeast(0L)
            }
            delay(500)
        }
    }

    var selectedTabIndex by remember { mutableIntStateOf(0) }
    var isSheetVisible by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val currentPlaylist = remember(selectedTabIndex) {
        when (selectedTabIndex) {
            0 -> durgaPujaTracks
            1 -> mahalayaTracks
            2 -> mahalayaSongsTracks
            else -> durgaPujaTracks
        }
    }

    // Dynamic countdown calculation until Durga Puja 2026 (Oct 16, 2026)
    var daysUntilPujo by remember { mutableIntStateOf(62) }
    LaunchedEffect(Unit) {
        val targetCal = Calendar.getInstance().apply {
            set(2026, Calendar.OCTOBER, 16, 0, 0, 0)
        }
        val diff = targetCal.timeInMillis - System.currentTimeMillis()
        val days = (diff / (1000 * 60 * 60 * 24)).toInt().coerceAtLeast(0)
        daysUntilPujo = if (days > 0) days else 62
    }

    val playTrackItem: (List<PujaTrack>, Int) -> Unit = { list, index ->
        resolvingIndex = index
        coroutineScope.launch {
            val tabTitle = when (selectedTabIndex) {
                0 -> "DURGA PUJA"
                1 -> "MAHALAYA"
                2 -> "MAHALAYA SONGS"
                else -> "DURGA PUJA"
            }
            val targetItem = resolvePujaTrackToMediaItem(list[index])
            val resolvedItems = withContext(Dispatchers.IO) {
                list.mapIndexed { i, track ->
                    if (i == index) targetItem else resolvePujaTrackToMediaItem(track)
                }
            }
            playerConnection?.playQueue(ListQueue(title = tabTitle, items = resolvedItems, startIndex = index))
            resolvingIndex = -1
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // Full screen immersive Kolkata Durga Puja Wallpaper
        Image(
            painter = painterResource(R.drawable.bg_durga_puja),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // Gradient overlay for readability
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = 0.25f),
                            Color.Black.copy(alpha = 0.45f),
                            Color.Black.copy(alpha = 0.85f)
                        )
                    )
                )
        )

        // Top Navigation Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.4f))
                    .clickable { navController.navigateUp() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(R.drawable.arrow_back),
                    contentDescription = stringResource(R.string.back_button_desc),
                    tint = Color.White
                )
            }
        }

        // Centerpiece: 3D Bengali Typography "পূজো আসছে" & Dynamic Countdown
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
                .padding(top = 110.dp, start = 24.dp, end = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 3D Bengali Typography with shadow offset depth
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = stringResource(R.string.pujo_aaschhe),
                    fontSize = 46.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF7A5400),
                    modifier = Modifier.padding(top = 3.dp, start = 3.dp)
                )
                Text(
                    text = stringResource(R.string.pujo_aaschhe),
                    fontSize = 46.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFE5B82A)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Dynamic Countdown Widget
            Text(
                text = stringResource(R.string.days_until_durga_pujo, daysUntilPujo),
                fontSize = 17.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White.copy(alpha = 0.95f),
                textAlign = TextAlign.Center
            )
        }

        // Persistent Floating Bottom Player Dock anchored at screen bottom
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top Pill Button: ":= DURGA PUJA v"
            Surface(
                onClick = { isSheetVisible = true },
                shape = RoundedCornerShape(20.dp),
                color = Color(0xFF4A342B).copy(alpha = 0.92f),
                contentColor = Color.White,
                shadowElevation = 8.dp,
                modifier = Modifier.padding(bottom = 10.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 18.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.list),
                        contentDescription = null,
                        tint = Color.White.copy(alpha = 0.9f),
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = stringResource(R.string.tab_durga_puja),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.8.sp
                    )
                    Icon(
                        painter = painterResource(R.drawable.expand_more),
                        contentDescription = null,
                        tint = Color.White.copy(alpha = 0.9f),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            // Glassmorphic Player Card
            val activeTrack = remember(currentMetadata, selectedTabIndex) {
                if (currentMetadata != null) {
                    PujaTrack(
                        title = currentMetadata.title,
                        artist = currentMetadata.artists.joinToString { it.name },
                        fallbackId = currentMetadata.id,
                        formattedDuration = formatTime(currentMetadata.duration * 1000L)
                    )
                } else {
                    durgaPujaTracks[0]
                }
            }

            val infiniteTransition = rememberInfiniteTransition(label = "disc_rotation")
            val rotationAngle by infiniteTransition.animateFloat(
                initialValue = 0f,
                targetValue = 360f,
                animationSpec = infiniteRepeatable(
                    animation = tween(durationMillis = 12000, easing = LinearEasing),
                    repeatMode = RepeatMode.Restart
                ),
                label = "rotation"
            )

            Surface(
                shape = RoundedCornerShape(24.dp),
                color = Color(0xFF261C18).copy(alpha = 0.88f),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.12f)),
                shadowElevation = 12.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Album Thumbnail with active rotation effect
                        val artworkUri = currentMetadata?.thumbnailUrl
                            ?: "https://i.ytimg.com/vi/${activeTrack.fallbackId ?: "V9n8kC78v-U"}/hqdefault.jpg"

                        Box(
                            modifier = Modifier
                                .size(58.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .border(1.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(14.dp))
                        ) {
                            AsyncImage(
                                model = artworkUri,
                                contentDescription = activeTrack.title,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .rotate(if (isPlaying) rotationAngle else 0f)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        // Title, Artist, Seekbar
                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = activeTrack.title,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = activeTrack.artist,
                                fontSize = 13.sp,
                                color = Color.White.copy(alpha = 0.7f),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            // Interactive Progress Line
                            Slider(
                                value = if (totalDuration > 0) currentPosition.toFloat() / totalDuration.toFloat() else 0f,
                                onValueChange = { percent ->
                                    if (totalDuration > 0) {
                                        val newPos = (percent * totalDuration).toLong()
                                        playerConnection?.player?.seekTo(newPos)
                                    }
                                },
                                colors = SliderDefaults.colors(
                                    thumbColor = Color(0xFFE5B82A),
                                    activeTrackColor = Color(0xFFE5B82A),
                                    inactiveTrackColor = Color.White.copy(alpha = 0.2f)
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(16.dp)
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Start
                            ) {
                                Text(
                                    text = "${formatTime(currentPosition)} / ${formatTime(totalDuration)}",
                                    fontSize = 11.sp,
                                    color = Color.White.copy(alpha = 0.6f)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        // Playback Controls
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            IconButton(
                                onClick = { playerConnection?.player?.seekToPrevious() }
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.skip_previous),
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(22.dp)
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color.White.copy(alpha = 0.25f))
                                    .clickable {
                                        val player = playerConnection?.player
                                        if (player != null) {
                                            if (isPlaying) player.pause() else player.play()
                                        } else {
                                            playTrackItem(durgaPujaTracks, 0)
                                        }
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    painter = painterResource(if (isPlaying) R.drawable.pause else R.drawable.play),
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(22.dp)
                                )
                            }

                            IconButton(
                                onClick = { playerConnection?.player?.seekToNext() }
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.skip_next),
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Divider(color = Color.White.copy(alpha = 0.1f), thickness = 1.dp)
                    Spacer(modifier = Modifier.height(8.dp))

                    // Bottom Row Action Buttons: Shuffle, Repeat, Dhak
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .clickable {
                                    val player = playerConnection?.player
                                    if (player != null) {
                                        player.shuffleModeEnabled = !player.shuffleModeEnabled
                                    }
                                }
                                .padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.shuffle),
                                contentDescription = null,
                                tint = Color.White.copy(alpha = 0.85f),
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = stringResource(R.string.puja_shuffle),
                                fontSize = 13.sp,
                                color = Color.White.copy(alpha = 0.85f)
                            )
                        }

                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .clickable {
                                    val player = playerConnection?.player
                                    if (player != null) {
                                        val newMode = if (player.repeatMode == androidx.media3.common.Player.REPEAT_MODE_OFF)
                                            androidx.media3.common.Player.REPEAT_MODE_ALL else androidx.media3.common.Player.REPEAT_MODE_OFF
                                        player.repeatMode = newMode
                                    }
                                }
                                .padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.repeat),
                                contentDescription = null,
                                tint = Color.White.copy(alpha = 0.85f),
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = stringResource(R.string.puja_repeat),
                                fontSize = 13.sp,
                                color = Color.White.copy(alpha = 0.85f)
                            )
                        }

                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .clickable {
                                    // Play authentic Dhaak track ("Dhak Baja Kashor Baja")
                                    playTrackItem(durgaPujaTracks, 75)
                                }
                                .padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.music_note),
                                contentDescription = null,
                                tint = Color(0xFFE5B82A),
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = stringResource(R.string.dhak),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFE5B82A)
                            )
                        }
                    }
                }
            }
        }

        // Expandable Playlist Sheet & 3-Tab Carousel matching Reference Image 4
        if (isSheetVisible) {
            ModalBottomSheet(
                onDismissRequest = { isSheetVisible = false },
                sheetState = sheetState,
                containerColor = Color(0xFF1E1614),
                contentColor = Color.White,
                shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 12.dp)
                ) {
                    // Header Row: PLAYLISTS & Close [X] Button
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(R.string.playlists_title),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.2.sp,
                            color = Color.White
                        )

                        IconButton(
                            onClick = { isSheetVisible = false }
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.close),
                                contentDescription = null,
                                tint = Color.White.copy(alpha = 0.8f),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // 3-Tab Carousel Selector: DURGA PUJA, MAHALAYA, MAHALAYA SONGS
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        val tabs = listOf(
                            stringResource(R.string.tab_durga_puja),
                            stringResource(R.string.tab_mahalaya),
                            stringResource(R.string.tab_mahalaya_songs)
                        )

                        tabs.forEachIndexed { index, title ->
                            val isSelected = selectedTabIndex == index
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(
                                        if (isSelected) Color(0xFF3B2A24) else Color.White.copy(alpha = 0.06f)
                                    )
                                    .clickable { selectedTabIndex = index }
                                    .padding(horizontal = 14.dp, vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = title,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) Color.White else Color.White.copy(alpha = 0.6f)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Tab Subtitle Description
                    val tabDescription = when (selectedTabIndex) {
                        0 -> stringResource(R.string.durga_puja_desc)
                        1 -> stringResource(R.string.mahalaya_desc)
                        2 -> stringResource(R.string.mahalaya_songs_desc)
                        else -> stringResource(R.string.durga_puja_desc)
                    }

                    Text(
                        text = tabDescription,
                        fontSize = 13.sp,
                        color = Color.White.copy(alpha = 0.65f)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Track List for Active Tab
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(380.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(bottom = 24.dp)
                    ) {
                        itemsIndexed(currentPlaylist) { index, track ->
                            val isCurrentPlaying = currentMetadata?.title == track.title
                            val isResolvingThis = resolvingIndex == index

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(
                                        if (isCurrentPlaying) Color(0xFF332520) else Color.Transparent
                                    )
                                    .clickable {
                                        playTrackItem(currentPlaylist, index)
                                        isSheetVisible = false
                                    }
                                    .padding(horizontal = 8.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Index Number 01, 02...
                                if (isResolvingThis) {
                                    CircularProgressIndicator(
                                        color = Color(0xFFE5B82A),
                                        strokeWidth = 2.dp,
                                        modifier = Modifier
                                            .size(20.dp)
                                            .padding(end = 8.dp)
                                    )
                                } else {
                                    Text(
                                        text = String.format(Locale.getDefault(), "%02d", index + 1),
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = if (isCurrentPlaying) Color(0xFFE5B82A) else Color.White.copy(alpha = 0.5f),
                                        modifier = Modifier.width(28.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.width(6.dp))

                                // Track Artwork Placeholder / Thumbnail
                                val trackThumb = "https://i.ytimg.com/vi/${track.fallbackId ?: "V9n8kC78v-U"}/hqdefault.jpg"

                                AsyncImage(
                                    model = trackThumb,
                                    contentDescription = track.title,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .size(46.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                )

                                Spacer(modifier = Modifier.width(12.dp))

                                // Title & Artist
                                Column(
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(
                                        text = track.title,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = if (isCurrentPlaying) Color(0xFFE5B82A) else Color.White,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        text = track.artist,
                                        fontSize = 12.sp,
                                        color = Color.White.copy(alpha = 0.6f),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }

                                Spacer(modifier = Modifier.width(8.dp))

                                // Track Duration
                                Text(
                                    text = track.formattedDuration,
                                    fontSize = 12.sp,
                                    color = Color.White.copy(alpha = 0.5f)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
