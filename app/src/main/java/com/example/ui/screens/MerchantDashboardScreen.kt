package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Store
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.FinancialRecordEntity
import com.example.ui.components.CommunicationActions
import com.example.ui.components.ConfirmationDeleteDialog
import com.example.ui.components.DealCardItem
import com.example.ui.theme.DinarGreen
import com.example.ui.theme.FireOrange
import com.example.ui.theme.FireRed
import com.example.ui.viewmodel.AjdabiyaViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MerchantDashboardScreen(
    viewModel: AjdabiyaViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val merchant by viewModel.currentMerchant.collectAsState()
    val merchantStore by viewModel.merchantStore.collectAsState()
    val deals by viewModel.merchantDeals.collectAsState()
    val financials by viewModel.merchantFinancials.collectAsState()
    val settings by viewModel.appSettings.collectAsState()

    var selectedSection by remember { mutableIntStateOf(0) } // 0: العروض, 1: كشف الحساب
    var showAddDealDialog by remember { mutableStateOf(false) }
    var showDeleteStoreConfirmDialog by remember { mutableStateOf(false) }
    var dealToDelete by remember { mutableStateOf<com.example.data.local.DealEntity?>(null) }

    BackHandler {
        viewModel.navigateBack()
    }

    if (merchant == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("يرجى تسجيل الدخول أولاً")
        }
        return
    }

    val currentMerchant = merchant!!
    val totalDue = financials.sumOf { it.cost }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "لوحة تحكم التاجر",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = currentMerchant.storeName,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 12.sp
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = { viewModel.navigateBack() },
                        modifier = Modifier.testTag("btn_back_merchant")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "الرجوع"
                        )
                    }
                },
                actions = {
                    // Delete Store Data Action (Protected with Confirmation Dialog)
                    IconButton(
                        onClick = { showDeleteStoreConfirmDialog = true },
                        modifier = Modifier.testTag("btn_delete_store_merchant")
                    ) {
                        Icon(
                            imageVector = Icons.Default.DeleteForever,
                            contentDescription = "حذف بيانات المحل",
                            tint = FireRed
                        )
                    }

                    // Logout
                    IconButton(
                        onClick = { viewModel.logoutMerchant() },
                        modifier = Modifier.testTag("btn_logout_merchant")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                            contentDescription = "تسجيل الخروج",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        floatingActionButton = {
            if (selectedSection == 0) {
                FloatingActionButton(
                    onClick = { showAddDealDialog = true },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = Color.White,
                    modifier = Modifier.testTag("fab_add_deal")
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "إضافة عرض جديد")
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Summary Banner
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "إجمالي المبلغ المستحق محلياً:",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Row(verticalAlignment = Alignment.Bottom) {
                            Text(
                                text = String.format(Locale.US, "%.1f", totalDue),
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.ExtraBold,
                                color = DinarGreen
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "د.ل",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = DinarGreen
                            )
                        }
                    }

                    Surface(
                        color = MaterialTheme.colorScheme.surface,
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                    ) {
                        Column(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "عدد العروض",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "${deals.size}",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }

            // Tabs: Deals (عروضي) vs Financial Statement (كشف الحساب الشهري)
            PrimaryTabRow(
                selectedTabIndex = selectedSection,
                containerColor = MaterialTheme.colorScheme.surface,
                indicator = {
                    TabRowDefaults.PrimaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(selectedSection),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            ) {
                Tab(
                    selected = selectedSection == 0,
                    onClick = { selectedSection = 0 },
                    modifier = Modifier.testTag("tab_merchant_deals"),
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.LocalOffer,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("عروض المتجر (${deals.size})")
                        }
                    }
                )

                Tab(
                    selected = selectedSection == 1,
                    onClick = { selectedSection = 1 },
                    modifier = Modifier.testTag("tab_merchant_financials"),
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Description,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("كشف الحساب الشهري 📋")
                        }
                    }
                )
            }

            when (selectedSection) {
                0 -> {
                    // Merchant Deals List
                    if (deals.isEmpty()) {
                        EmptyStateView(
                            icon = Icons.Default.LocalOffer,
                            title = "لا توجد عروض منشورة لمتجرك",
                            subtitle = "اضغط على زر (+) بالأسفل لإضافة أول عرض لمتجرك في أجدابيا"
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            items(deals, key = { it.id }) { deal ->
                                DealCardItem(
                                    deal = deal,
                                    onDeleteClick = { dealToDelete = deal }
                                )
                            }
                        }
                    }
                }
                1 -> {
                    // Detailed Monthly Financial Statement (كشف الحساب الشهري المفصل)
                    if (financials.isEmpty()) {
                        EmptyStateView(
                            icon = Icons.Default.Payments,
                            title = "لا توجد سجلات مالية بعد",
                            subtitle = "يتم تسجيل كل إعلان تنشره تلقائياً في كشف الحساب بالتاريخ والتكلفة"
                        )
                    } else {
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .testTag("financial_statement_list"),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            item {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "جدول العمليات المالية المفصلة:",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "المجموع: ${String.format(Locale.US, "%.1f", totalDue)} د.ل",
                                        style = MaterialTheme.typography.labelLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = DinarGreen
                                    )
                                }
                            }

                            items(financials, key = { it.id }) { record ->
                                FinancialRecordCard(record = record)
                            }

                            item {
                                Spacer(modifier = Modifier.height(10.dp))
                                // Contact Owner for Settlement
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                                    ),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(14.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = "تسوية الحساب مع إدارة التطبيق",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 13.sp
                                            )
                                            Text(
                                                text = "هاتف المالك: ${settings.ownerPhone}",
                                                fontSize = 12.sp,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }

                                        Button(
                                            onClick = {
                                                CommunicationActions.openDialer(context, settings.ownerPhone)
                                            },
                                            shape = RoundedCornerShape(8.dp),
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = MaterialTheme.colorScheme.primary
                                            )
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Call,
                                                contentDescription = null,
                                                modifier = Modifier.size(14.dp)
                                            )
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("اتصال", fontSize = 12.sp)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Add Deal Dialog
    if (showAddDealDialog) {
        AddDealDialog(
            regularPrice = settings.regularPrice,
            hotPrice = settings.hotPrice,
            onDismiss = { showAddDealDialog = false },
            onPublish = { content, fbUrl, isHot ->
                viewModel.publishDeal(
                    content = content,
                    facebookPostUrl = fbUrl,
                    isHot = isHot,
                    onSuccess = {
                        showAddDealDialog = false
                    }
                )
            }
        )
    }

    // Confirm Delete Store Data Dialog
    if (showDeleteStoreConfirmDialog) {
        val storeTitle = merchantStore?.name ?: currentMerchant.storeName
        ConfirmationDeleteDialog(
            title = "تأكيد حذف بيانات المحل",
            message = "هل أنت متأكد من رغبتك في حذف بيانات متجرك \"$storeTitle\" نهائياً من قاعدة البيانات المحلية؟ سيتم حذف بيانات المحل وكافة العروض المرتبطة به ولن تتمكن من التراجع عن هذا الإجراء.",
            confirmButtonText = "نعم، حذف المحل نهائياً",
            onConfirm = {
                showDeleteStoreConfirmDialog = false
                viewModel.deleteMerchant(currentMerchant.username)
                viewModel.logoutMerchant()
            },
            onDismiss = {
                showDeleteStoreConfirmDialog = false
            }
        )
    }

    // Confirm Delete Deal Dialog
    if (dealToDelete != null) {
        ConfirmationDeleteDialog(
            title = "تأكيد حذف العرض",
            message = "هل أنت متأكد من رغبتك في حذف هذا العرض نهائياً من قاعدة البيانات المحلية؟",
            confirmButtonText = "نعم، حذف العرض",
            onConfirm = {
                val dealId = dealToDelete?.id ?: 0L
                dealToDelete = null
                viewModel.deleteDeal(dealId)
            },
            onDismiss = {
                dealToDelete = null
            }
        )
    }
}

@Composable
fun FinancialRecordCard(record: FinancialRecordEntity) {
    val dateStr = SimpleDateFormat("yyyy/MM/dd - hh:mm a", Locale("ar")).format(Date(record.timestamp))
    val isHot = record.dealType == "ساخن"

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        color = if (isHot) FireRed.copy(alpha = 0.12f) else MaterialTheme.colorScheme.primaryContainer,
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = "إعلان ${record.dealType}",
                            color = if (isHot) FireRed else MaterialTheme.colorScheme.primary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = dateStr,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = record.dealContentPreview,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 2
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "${String.format(Locale.US, "%.1f", record.cost)} د.ل",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = DinarGreen
                )
                Text(
                    text = "مستحق محلياً",
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun AddDealDialog(
    regularPrice: Double,
    hotPrice: Double,
    onDismiss: () -> Unit,
    onPublish: (String, String, Boolean) -> Unit
) {
    var content by remember { mutableStateOf("") }
    var facebookPostUrl by remember { mutableStateOf("") }
    var isHot by remember { mutableStateOf(false) } // false = عادي (3 د.ل), true = ساخن (5 د.ل)
    var errorMessage by remember { mutableStateOf<String?>(null) }

    fun submit() {
        if (content.isBlank()) {
            errorMessage = "يرجى كتابة نص العرض والتخفيض"
            return
        }
        onPublish(content, facebookPostUrl, isHot)
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "إضافة عرض جديد للمتجر",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "سيتم حفظ العرض وتسجيل تكلفته محلياً في كشف الحساب:",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Deal Content Field
                OutlinedTextField(
                    value = content,
                    onValueChange = {
                        content = it
                        errorMessage = null
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("deal_content_input"),
                    label = { Text("تفاصيل العرض والتخفيض *") },
                    placeholder = { Text("مثال: تخفيض 30% على كافة السلع حتى نهاية الأسبوع...") },
                    minLines = 3,
                    maxLines = 5
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Facebook Post URL Field
                OutlinedTextField(
                    value = facebookPostUrl,
                    onValueChange = { facebookPostUrl = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("deal_fb_url_input"),
                    label = { Text("رابط منشور الفيسبوك (اختياري)") },
                    placeholder = { Text("https://facebook.com/...") },
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Link, contentDescription = null)
                    },
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Ad Type Selection
                Text(
                    text = "اختر نوع الإعلان:",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Option 1: Regular
                Card(
                    onClick = { isHot = false },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (!isHot) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
                        else MaterialTheme.colorScheme.surface
                    ),
                    border = BorderStroke(
                        1.dp,
                        if (!isHot) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = !isHot,
                            onClick = { isHot = false }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "إعلان عادي",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            Text(
                                text = "يظهر في صفحة المحل والتخفيضات العامة",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Text(
                            text = "${regularPrice.toInt()} دينار",
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 13.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Option 2: Hot Deal 🔥
                Card(
                    onClick = { isHot = true },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isHot) FireRed.copy(alpha = 0.1f)
                        else MaterialTheme.colorScheme.surface
                    ),
                    border = BorderStroke(
                        1.dp,
                        if (isHot) FireRed else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = isHot,
                            onClick = { isHot = true }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "إعلان ساخن 🔥",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = if (isHot) FireRed else MaterialTheme.colorScheme.onSurface
                                )
                            }
                            Text(
                                text = "يظهر في التبويب الرئيسي (العروض الساخنة)",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Text(
                            text = "${hotPrice.toInt()} دينار",
                            fontWeight = FontWeight.Bold,
                            color = FireRed,
                            fontSize = 13.sp
                        )
                    }
                }

                if (errorMessage != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = errorMessage ?: "",
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 12.sp
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { submit() },
                modifier = Modifier.testTag("btn_publish_deal_confirm")
            ) {
                Text("نشر وتثبيت العرض")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("إلغاء")
            }
        },
        shape = RoundedCornerShape(18.dp)
    )
}
