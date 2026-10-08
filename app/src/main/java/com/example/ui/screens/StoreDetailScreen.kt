package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.CommunicationActions
import com.example.ui.components.DealCardItem
import com.example.ui.theme.DinarGreen
import com.example.ui.theme.FireOrange
import com.example.ui.viewmodel.AjdabiyaViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StoreDetailScreen(
    viewModel: AjdabiyaViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val store by viewModel.selectedStore.collectAsState()
    val deals by viewModel.selectedStoreDeals.collectAsState()
    val followedStoreIds by viewModel.followedStoreIds.collectAsState()

    BackHandler {
        viewModel.navigateBack()
    }

    if (store == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("المحل غير متوفر")
        }
        return
    }

    val currentStore = store!!
    val isFollowed = followedStoreIds.contains(currentStore.id)

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = currentStore.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = { viewModel.navigateBack() },
                        modifier = Modifier.testTag("btn_back_store_detail")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "الرجوع"
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = { viewModel.toggleFollowStore(currentStore.id, currentStore.name) },
                        modifier = Modifier.testTag("btn_toggle_follow_topbar")
                    ) {
                        Icon(
                            imageVector = if (isFollowed) Icons.Default.NotificationsActive else Icons.Default.NotificationsNone,
                            contentDescription = if (isFollowed) "إلغاء المتابعة" else "متابعة المتجر",
                            tint = if (isFollowed) FireOrange else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Store Overview Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(56.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primaryContainer),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = currentStore.name.take(1),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 24.sp,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }

                            Spacer(modifier = Modifier.width(14.dp))

                            Column {
                                Text(
                                    text = currentStore.name,
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )

                                Spacer(modifier = Modifier.height(4.dp))

                                Surface(
                                    color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f),
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = currentStore.category,
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Location
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = currentStore.address,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Quick Actions
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Call
                            Button(
                                onClick = { CommunicationActions.openDialer(context, currentStore.phone) },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Call,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("اتصال", fontSize = 12.sp)
                            }

                            // WhatsApp
                            OutlinedButton(
                                onClick = {
                                    val wa = if (currentStore.whatsapp.isNotBlank()) currentStore.whatsapp else currentStore.phone
                                    CommunicationActions.openWhatsApp(
                                        context = context,
                                        phone = wa,
                                        message = "السلام عليكم، بخصوص متجركم (${currentStore.name}):"
                                    )
                                },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(10.dp),
                                border = BorderStroke(1.dp, DinarGreen)
                            ) {
                                Text("💬", fontSize = 14.sp)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("واتساب", fontSize = 12.sp, color = DinarGreen)
                            }

                            // Facebook Page
                            if (currentStore.facebookUrl.isNotBlank()) {
                                OutlinedButton(
                                    onClick = { CommunicationActions.openFacebookPost(context, currentStore.facebookUrl) },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(10.dp),
                                    border = BorderStroke(1.dp, Color(0xFF1877F2))
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Language,
                                        contentDescription = null,
                                        tint = Color(0xFF1877F2),
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("الصفحة", fontSize = 12.sp, color = Color(0xFF1877F2))
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Follow Store Banner Action
                        OutlinedButton(
                            onClick = { viewModel.toggleFollowStore(currentStore.id, currentStore.name) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("btn_toggle_follow_store_detail"),
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(
                                1.5.dp,
                                if (isFollowed) FireOrange else MaterialTheme.colorScheme.primary
                            ),
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = if (isFollowed) FireOrange.copy(alpha = 0.1f) else Color.Transparent
                            )
                        ) {
                            Icon(
                                imageVector = if (isFollowed) Icons.Default.NotificationsActive else Icons.Default.NotificationsNone,
                                contentDescription = null,
                                tint = if (isFollowed) FireOrange else MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isFollowed) "⭐ أنت تتابع هذا المتجر (إلغاء المتابعة)" else "🔔 متابعة المتجر لتلقي إشعارات العروض الجديدة",
                                fontWeight = FontWeight.Bold,
                                color = if (isFollowed) FireOrange else MaterialTheme.colorScheme.primary,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }

            // Section Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.LocalOffer,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "عروض وتخفيضات المتجر (${deals.size})",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            // Store Deals List
            if (deals.isEmpty()) {
                item {
                    EmptyStateView(
                        icon = Icons.Default.LocalOffer,
                        title = "لا توجد عروض حالياً لهذا المتجر",
                        subtitle = "تابعنا باستمرار للاطلاع على أحدث عروض وتخفيضات ${currentStore.name}"
                    )
                }
            } else {
                items(deals, key = { it.id }) { deal ->
                    DealCardItem(deal = deal, isFollowed = isFollowed)
                }
            }
        }
    }
}
