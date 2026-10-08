package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Store
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.DealEntity
import com.example.ui.theme.DinarGreen
import com.example.ui.theme.FireOrange
import com.example.ui.theme.FireRed
import com.example.ui.theme.LocalAjdabiyaCustomColors
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun DealCardItem(
    deal: DealEntity,
    modifier: Modifier = Modifier,
    isFollowed: Boolean = false,
    onStoreClick: (() -> Unit)? = null,
    onDeleteClick: (() -> Unit)? = null
) {
    val context = LocalContext.current
    val customColors = LocalAjdabiyaCustomColors.current
    val isHot = deal.dealType == "HOT"
    val formattedDate = SimpleDateFormat("yyyy/MM/dd - hh:mm a", Locale("ar")).format(Date(deal.timestamp))

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("deal_card_${deal.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isHot) 3.dp else 1.dp),
        border = if (isHot) {
            BorderStroke(1.5.dp, customColors.hotDealBorder)
        } else {
            BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header: Store Name + Deal Type Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .weight(1f)
                        .then(
                            if (onStoreClick != null) Modifier.clickable { onStoreClick() }
                            else Modifier
                        )
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(
                                if (isHot) FireRed.copy(alpha = 0.12f)
                                else MaterialTheme.colorScheme.primaryContainer
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isHot) Icons.Default.LocalFireDepartment else Icons.Default.Store,
                            contentDescription = null,
                            tint = if (isHot) FireRed else MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = deal.storeName,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            if (isFollowed) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    color = FireOrange.copy(alpha = 0.15f),
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = "⭐ متابع",
                                        color = FireOrange,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }
                        Text(
                            text = formattedDate,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                        )
                    }
                }

                // Hot Deal Badge or Regular
                if (isHot) {
                    Surface(
                        color = customColors.hotDealBadgeBackground,
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier.padding(start = 8.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocalFireDepartment,
                                contentDescription = null,
                                tint = customColors.hotDealBadgeText,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "عرض ساخن",
                                color = customColors.hotDealBadgeText,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                if (onDeleteClick != null) {
                    IconButton(
                        onClick = onDeleteClick,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "حذف العرض",
                            tint = FireRed
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Deal Content Text
            Text(
                text = deal.content,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
                lineHeight = 24.sp,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Direct Facebook Post Button
            if (deal.facebookPostUrl.isNotBlank()) {
                Button(
                    onClick = {
                        CommunicationActions.openFacebookPost(context, deal.facebookPostUrl)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("btn_fb_deal_${deal.id}"),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF1877F2), // Official Facebook Brand Blue
                        contentColor = Color.White
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.OpenInNew,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "مشاهدة المنشور الأصلي على فيسبوك",
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))
            }

            // Quick Contact Buttons Row: Call, WhatsApp, Messenger
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Call
                OutlinedButton(
                    onClick = {
                        CommunicationActions.openDialer(context, deal.phone)
                    },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("btn_call_${deal.id}"),
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.5f))
                ) {
                    Icon(
                        imageVector = Icons.Default.Call,
                        contentDescription = "اتصال",
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "اتصال",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                // WhatsApp
                OutlinedButton(
                    onClick = {
                        val waNumber = if (deal.whatsapp.isNotBlank()) deal.whatsapp else deal.phone
                        CommunicationActions.openWhatsApp(
                            context = context,
                            phone = waNumber,
                            message = "السلام عليكم، بخصوص عرضكم (${deal.storeName}) على تطبيق تخفيضات أجدابيا:"
                        )
                    },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("btn_wa_${deal.id}"),
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, DinarGreen.copy(alpha = 0.5f))
                ) {
                    Text(
                        text = "💬",
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "واتساب",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = DinarGreen
                    )
                }

                // Messenger
                OutlinedButton(
                    onClick = {
                        val mUrl = if (deal.messengerUrl.isNotBlank()) deal.messengerUrl else deal.facebookPostUrl
                        CommunicationActions.openMessenger(context, mUrl)
                    },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("btn_msg_${deal.id}"),
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, Color(0xFF0084FF).copy(alpha = 0.5f))
                ) {
                    Icon(
                        imageVector = Icons.Default.Email,
                        contentDescription = "ماسنجر",
                        modifier = Modifier.size(16.dp),
                        tint = Color(0xFF0084FF)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "ماسنجر",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0084FF)
                    )
                }
            }
        }
    }
}
