package com.example.ui.screens

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.PriceChange
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Store
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.PrimaryTabRow
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.ActivationCodeEntity
import com.example.data.local.MerchantEntity
import com.example.ui.components.ConfirmationDeleteDialog
import com.example.ui.theme.DinarGreen
import com.example.ui.theme.FireOrange
import com.example.ui.theme.FireRed
import com.example.ui.viewmodel.AjdabiyaViewModel
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MasterAdminScreen(
    viewModel: AjdabiyaViewModel,
    modifier: Modifier = Modifier
) {
    val settings by viewModel.appSettings.collectAsState()
    val merchants by viewModel.allMerchants.collectAsState()
    val financials by viewModel.allFinancials.collectAsState()
    val activationCodes by viewModel.allActivationCodes.collectAsState()

    var selectedTab by remember { mutableIntStateOf(0) } // 0: الإعدادات والأسعار, 1: حسابات التجار, 2: رموز التفعيل
    var showCreateMerchantDialog by remember { mutableStateOf(false) }
    var editingMerchant by remember { mutableStateOf<MerchantEntity?>(null) }
    var merchantToDelete by remember { mutableStateOf<MerchantEntity?>(null) }
    var activationCodeToDelete by remember { mutableStateOf<ActivationCodeEntity?>(null) }
    var newlyGeneratedCode by remember { mutableStateOf<ActivationCodeEntity?>(null) }
    var selectedPlanType by remember { mutableStateOf("اشتراك شهري كامل") }
    var planDropdownExpanded by remember { mutableStateOf(false) }
    var adminMerchantSearchQuery by remember { mutableStateOf("") }
    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current

    // Pricing & Phone local state for editing
    var regularPriceInput by remember(settings.regularPrice) {
        mutableStateOf(settings.regularPrice.toInt().toString())
    }
    var hotPriceInput by remember(settings.hotPrice) {
        mutableStateOf(settings.hotPrice.toInt().toString())
    }
    var ownerPhoneInput by remember(settings.ownerPhone) {
        mutableStateOf(settings.ownerPhone)
    }

    val totalRevenue = financials.sumOf { it.cost }

    BackHandler {
        viewModel.navigateBack()
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.AdminPanelSettings,
                            contentDescription = null,
                            tint = FireRed
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "لوحة تحكم المالك السرية",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = { viewModel.navigateBack() },
                        modifier = Modifier.testTag("btn_back_admin")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "الرجوع"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        floatingActionButton = {
            if (selectedTab == 1) {
                FloatingActionButton(
                    onClick = { showCreateMerchantDialog = true },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = Color.White,
                    modifier = Modifier.testTag("fab_create_merchant")
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "إضافة تاجر جديد")
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Master Revenue Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                ),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
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
                            text = "إجمالي عوائد الإعلانات المحلية:",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Row(verticalAlignment = Alignment.Bottom) {
                            Text(
                                text = String.format(Locale.US, "%.1f", totalRevenue),
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
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "التجار المسجلين",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "${merchants.size}",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }

            // Tabs: Settings & Pricing vs Merchant Accounts
            PrimaryTabRow(
                selectedTabIndex = selectedTab,
                containerColor = MaterialTheme.colorScheme.surface,
                indicator = {
                    TabRowDefaults.PrimaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(selectedTab),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    modifier = Modifier.testTag("tab_admin_settings"),
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.PriceChange, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("الأسعار وبيانات المالك")
                        }
                    }
                )

                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    modifier = Modifier.testTag("tab_admin_merchants"),
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Store, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("حسابات التجار (${merchants.size})")
                        }
                    }
                )

                Tab(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    modifier = Modifier.testTag("tab_admin_activation_codes"),
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.VpnKey, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("رموز التفعيل (${activationCodes.size})")
                        }
                    }
                )
            }

            when (selectedTab) {
                0 -> {
                    // Settings & Pricing Tab
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surface
                                ),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.25f))
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(18.dp)
                                ) {
                                    Text(
                                        text = "تعديل أسعار الإعلانات محلياً:",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "يتم تطبيق الأسعار فورياً على كافة حسابات التجار في أجدابيا.",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )

                                    Spacer(modifier = Modifier.height(16.dp))

                                    // Regular Ad Price
                                    OutlinedTextField(
                                        value = regularPriceInput,
                                        onValueChange = { regularPriceInput = it },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .testTag("input_regular_price"),
                                        label = { Text("سعر الإعلان العادي (دينار ليبي)") },
                                        trailingIcon = { Text("د.ل", modifier = Modifier.padding(end = 12.dp), fontWeight = FontWeight.Bold) },
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                        singleLine = true
                                    )

                                    Spacer(modifier = Modifier.height(12.dp))

                                    // Hot Ad Price
                                    OutlinedTextField(
                                        value = hotPriceInput,
                                        onValueChange = { hotPriceInput = it },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .testTag("input_hot_price"),
                                        label = { Text("سعر الإعلان الساخن 🔥 (دينار ليبي)") },
                                        trailingIcon = { Text("د.ل", modifier = Modifier.padding(end = 12.dp), fontWeight = FontWeight.Bold) },
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                        singleLine = true
                                    )

                                    Spacer(modifier = Modifier.height(20.dp))

                                    HorizontalDivider()

                                    Spacer(modifier = Modifier.height(16.dp))

                                    Text(
                                        text = "رقم هاتف المالك الرسمي للتواصل والتسوية:",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold
                                    )

                                    Spacer(modifier = Modifier.height(8.dp))

                                    OutlinedTextField(
                                        value = ownerPhoneInput,
                                        onValueChange = { ownerPhoneInput = it },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .testTag("input_owner_phone"),
                                        label = { Text("رقم هاتف المالك") },
                                        leadingIcon = {
                                            Icon(imageVector = Icons.Default.Call, contentDescription = null)
                                        },
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                                        singleLine = true
                                    )

                                    Spacer(modifier = Modifier.height(20.dp))

                                    Button(
                                        onClick = {
                                            val regular = regularPriceInput.toDoubleOrNull() ?: settings.regularPrice
                                            val hot = hotPriceInput.toDoubleOrNull() ?: settings.hotPrice
                                            viewModel.updatePricingAndPhone(regular, hot, ownerPhoneInput)
                                        },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .testTag("btn_save_admin_settings"),
                                        shape = RoundedCornerShape(10.dp)
                                    ) {
                                        Icon(imageVector = Icons.Default.Save, contentDescription = null)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("حفظ التعديلات في التخزين المحلي", fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }

                1 -> {
                    // Merchants List Tab with Search by store name or discount type
                    val filteredMerchants = merchants.filter {
                        if (adminMerchantSearchQuery.isBlank()) true
                        else {
                            val q = adminMerchantSearchQuery.trim()
                            it.storeName.contains(q, ignoreCase = true) ||
                            it.discountType.contains(q, ignoreCase = true) ||
                            it.username.contains(q, ignoreCase = true) ||
                            it.category.contains(q, ignoreCase = true)
                        }
                    }

                    Column(modifier = Modifier.fillMaxSize()) {
                        // Search bar inside merchants list in admin
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
                            )
                        ) {
                            OutlinedTextField(
                                value = adminMerchantSearchQuery,
                                onValueChange = { adminMerchantSearchQuery = it },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp)
                                    .testTag("admin_merchant_search_field"),
                                placeholder = {
                                    Text("ابحث باسم المحل أو نوع التخفيضات أو اسم المستخدم...", fontSize = 12.sp)
                                },
                                leadingIcon = {
                                    Icon(Icons.Default.Search, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                },
                                trailingIcon = {
                                    if (adminMerchantSearchQuery.isNotEmpty()) {
                                        IconButton(onClick = { adminMerchantSearchQuery = "" }) {
                                            Icon(Icons.Default.Clear, contentDescription = "مسح")
                                        }
                                    }
                                },
                                singleLine = true,
                                shape = RoundedCornerShape(10.dp)
                            )
                        }

                        if (filteredMerchants.isEmpty()) {
                            EmptyStateView(
                                icon = Icons.Default.Store,
                                title = if (adminMerchantSearchQuery.isNotEmpty()) "لا توجد نتائج مطابقة لبحثك" else "لا توجد حسابات تجار",
                                subtitle = if (adminMerchantSearchQuery.isNotEmpty()) "جرّب البحث باسم محل أو نوع تخفيضات آخر" else "اضغط على زر (+) بالأسفل لإضافة وتوليد أول حساب تاجر"
                            )
                        } else {
                            LazyColumn(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .testTag("admin_merchants_list"),
                                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 4.dp, bottom = 80.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                items(filteredMerchants, key = { it.id }) { m ->
                                    MerchantAccountCard(
                                        merchant = m,
                                        onEdit = { editingMerchant = m },
                                        onDelete = { merchantToDelete = m }
                                    )
                                }
                            }
                        }
                    }
                }

                2 -> {
                    // Activation Codes Tab (خيار توليد رموز التفعيل داخل قائمة المالك)
                    val planOptions = listOf(
                        "اشتراك شهري كامل",
                        "باقة 10 إعلانات",
                        "اشتراك سنوي ذهبي",
                        "تفعيل تجريبي مجاني"
                    )

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                            .testTag("activation_codes_list"),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        // Generator Card
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surface
                                ),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.25f))
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(18.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.VpnKey,
                                            contentDescription = null,
                                            tint = FireOrange,
                                            modifier = Modifier.size(24.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "توليد رموز التفعيل للاشتراكات",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "قم بتوليد رموز تفعيل فريدة لمنحها للتجار في أجدابيا لتفعيل حساباتهم وباقاتهم محلياً.",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )

                                    Spacer(modifier = Modifier.height(14.dp))

                                    // Plan selection dropdown
                                    ExposedDropdownMenuBox(
                                        expanded = planDropdownExpanded,
                                        onExpandedChange = { planDropdownExpanded = !planDropdownExpanded }
                                    ) {
                                        OutlinedTextField(
                                            value = selectedPlanType,
                                            onValueChange = {},
                                            readOnly = true,
                                            label = { Text("نوع باقة التفعيل") },
                                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = planDropdownExpanded) },
                                            modifier = Modifier
                                                .menuAnchor()
                                                .fillMaxWidth(),
                                            shape = RoundedCornerShape(10.dp)
                                        )
                                        ExposedDropdownMenu(
                                            expanded = planDropdownExpanded,
                                            onDismissRequest = { planDropdownExpanded = false }
                                        ) {
                                            planOptions.forEach { option ->
                                                DropdownMenuItem(
                                                    text = { Text(option) },
                                                    onClick = {
                                                        selectedPlanType = option
                                                        planDropdownExpanded = false
                                                    }
                                                )
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(16.dp))

                                    Button(
                                        onClick = {
                                            viewModel.generateActivationCode(selectedPlanType) { code ->
                                                newlyGeneratedCode = code
                                            }
                                        },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .testTag("btn_generate_activation_code"),
                                        shape = RoundedCornerShape(10.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = FireOrange
                                        )
                                    ) {
                                        Icon(imageVector = Icons.Default.Add, contentDescription = null)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("توليد رمز تفعيل جديد الآن 🔑", fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }

                        // Activation codes header
                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "الرموز الموّلدة (${activationCodes.size}):",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }

                        if (activationCodes.isEmpty()) {
                            item {
                                EmptyStateView(
                                    icon = Icons.Default.VpnKey,
                                    title = "لا توجد رموز تفعيل بعد",
                                    subtitle = "اضغط على زر التوليد أعلاه لإنشاء أول رمز تفعيل للاشتراكات"
                                )
                            }
                        } else {
                            items(activationCodes, key = { it.id }) { code ->
                                ActivationCodeItemCard(
                                    code = code,
                                    onCopy = {
                                        clipboardManager.setText(AnnotatedString(code.code))
                                        Toast.makeText(context, "تم نسخ الرمز: ${code.code}", Toast.LENGTH_SHORT).show()
                                    },
                                    onDelete = { activationCodeToDelete = code }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Confirm Delete Merchant Dialog
    if (merchantToDelete != null) {
        ConfirmationDeleteDialog(
            title = "تأكيد حذف بيانات المحل",
            message = "هل أنت متأكد من رغبتك في حذف محل \"${merchantToDelete?.storeName}\" من قاعدة البيانات المحلية؟ سيتم حذف بيانات المتجر وكافة العروض المرتبطة به نهائياً.",
            confirmButtonText = "نعم، حذف المحل",
            onConfirm = {
                val username = merchantToDelete?.username ?: ""
                merchantToDelete = null
                viewModel.deleteMerchant(username)
            },
            onDismiss = {
                merchantToDelete = null
            }
        )
    }

    // Newly Generated Activation Code Dialog
    if (newlyGeneratedCode != null) {
        val codeObj = newlyGeneratedCode!!
        AlertDialog(
            onDismissRequest = { newlyGeneratedCode = null },
            icon = {
                Icon(
                    imageVector = Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = DinarGreen,
                    modifier = Modifier.size(36.dp)
                )
            },
            title = {
                Text(
                    text = "تم توليد رمز التفعيل بنجاح! 🔑",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "رمز التفعيل لباقة \"${codeObj.planType}\":",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Surface(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = codeObj.code,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 22.sp,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "يمكنك نسخ الرمز وإرساله للتاجر عبر واتساب أو رسالة نصية لتفعيل حسابه في أجدابيا.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        clipboardManager.setText(AnnotatedString(codeObj.code))
                        Toast.makeText(context, "تم نسخ الرمز: ${codeObj.code}", Toast.LENGTH_SHORT).show()
                        newlyGeneratedCode = null
                    },
                    modifier = Modifier.testTag("btn_copy_new_code")
                ) {
                    Icon(imageVector = Icons.Default.ContentCopy, contentDescription = null)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("نسخ الرمز وإغلاق")
                }
            },
            dismissButton = {
                TextButton(onClick = { newlyGeneratedCode = null }) {
                    Text("إغلاق")
                }
            },
            shape = RoundedCornerShape(18.dp)
        )
    }

    // Confirm Delete Activation Code Dialog
    if (activationCodeToDelete != null) {
        val codeObj = activationCodeToDelete!!
        ConfirmationDeleteDialog(
            title = "تأكيد حذف رمز التفعيل",
            message = "هل أنت متأكد من رغبتك في حذف رمز التفعيل \"${codeObj.code}\" نهائياً من قاعدة البيانات المحلية؟",
            confirmButtonText = "نعم، حذف الرمز",
            onConfirm = {
                val id = codeObj.id
                activationCodeToDelete = null
                viewModel.deleteActivationCode(id)
            },
            onDismiss = {
                activationCodeToDelete = null
            }
        )
    }

    // Edit Existing Merchant Dialog (prepopulated with current data)
    if (editingMerchant != null) {
        MerchantFormDialog(
            merchantToEdit = editingMerchant,
            onDismiss = { editingMerchant = null },
            onSave = { username, password, storeName, phone, category, address, whatsapp, facebook, location, discountType ->
                viewModel.updateMerchantAccount(
                    username = username,
                    newPassword = password,
                    storeName = storeName,
                    phone = phone,
                    category = category,
                    whatsapp = whatsapp,
                    facebookUrl = facebook,
                    location = location,
                    discountType = discountType,
                    onResult = { success ->
                        if (success) {
                            editingMerchant = null
                        }
                    }
                )
            }
        )
    }

    // Create New Merchant Dialog
    if (showCreateMerchantDialog) {
        MerchantFormDialog(
            merchantToEdit = null,
            onDismiss = { showCreateMerchantDialog = false },
            onSave = { username, password, storeName, phone, category, address, whatsapp, facebook, location, discountType ->
                viewModel.createMerchantAccount(
                    username = username,
                    password = password,
                    storeName = storeName,
                    phone = phone,
                    category = category,
                    address = address,
                    whatsapp = whatsapp,
                    facebookUrl = facebook,
                    location = location,
                    discountType = discountType,
                    onResult = { success ->
                        if (success) {
                            showCreateMerchantDialog = false
                        }
                    }
                )
            }
        )
    }
}

@Composable
fun MerchantAccountCard(
    merchant: MerchantEntity,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onEdit() }
            .testTag("merchant_item_${merchant.id}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = merchant.storeName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "الموقع: ${merchant.location}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "نوع التخفيضات: ${merchant.discountType}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onEdit) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "تعديل بيانات التاجر",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }

                    IconButton(onClick = onDelete) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "حذف حساب التاجر",
                            tint = FireRed
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "اسم الدخول: ${merchant.username}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "كلمة المرور: ${merchant.password}",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "هاتف: ${merchant.phone}",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Composable
fun MerchantFormDialog(
    merchantToEdit: MerchantEntity? = null,
    onDismiss: () -> Unit,
    onSave: (username: String, pass: String, store: String, phone: String, cat: String, addr: String, wa: String, fb: String, location: String, discountType: String) -> Unit
) {
    val isEditing = merchantToEdit != null
    var username by remember(merchantToEdit) { mutableStateOf(merchantToEdit?.username ?: "") }
    var password by remember(merchantToEdit) { mutableStateOf(merchantToEdit?.password ?: "") }
    var storeName by remember(merchantToEdit) { mutableStateOf(merchantToEdit?.storeName ?: "") }
    var location by remember(merchantToEdit) { mutableStateOf(merchantToEdit?.location ?: "أجدابيا - وسط المدينة") }
    var discountType by remember(merchantToEdit) { mutableStateOf(merchantToEdit?.discountType ?: "عروض ساخنة وتخفيضات موسمية") }
    var phone by remember(merchantToEdit) { mutableStateOf(merchantToEdit?.phone ?: "") }
    var category by remember(merchantToEdit) { mutableStateOf(merchantToEdit?.category ?: "مواد غذائية") }
    var address by remember(merchantToEdit) { mutableStateOf(merchantToEdit?.address ?: "أجدابيا") }
    var whatsapp by remember(merchantToEdit) { mutableStateOf("") }
    var facebookUrl by remember(merchantToEdit) { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    fun submit() {
        if (username.isBlank() || password.isBlank() || storeName.isBlank() || phone.isBlank()) {
            errorMessage = "يرجى تعبئة الحقول الأساسية المطلوبة (*)"
            return
        }
        onSave(
            username.trim(),
            password.trim(),
            storeName.trim(),
            phone.trim(),
            category.trim(),
            address.trim(),
            whatsapp.trim().ifBlank { phone.trim() },
            facebookUrl.trim().ifBlank { "https://facebook.com" },
            location.trim().ifBlank { address.trim() },
            discountType.trim().ifBlank { "تخفيضات وعروض خاصة" }
        )
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (isEditing) "تعديل بيانات التاجر" else "إضافة وتوليد حساب تاجر جديد",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    Text(
                        text = if (isEditing) "قم بتعديل بيانات المحل ونوع التخفيضات في قاعدة البيانات المحلية:"
                               else "سيتم حفظ الحساب وإنشاء المتجر فورياً في التخزين المحلي:",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                item {
                    OutlinedTextField(
                        value = username,
                        onValueChange = { if (!isEditing) username = it },
                        readOnly = isEditing,
                        modifier = Modifier.fillMaxWidth().testTag("input_merchant_form_username"),
                        label = { Text(if (isEditing) "اسم المستخدم (معرف الحساب)" else "اسم المستخدم للدخول *") },
                        singleLine = true
                    )
                }

                item {
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        modifier = Modifier.fillMaxWidth().testTag("input_merchant_form_password"),
                        label = { Text("كلمة المرور *") },
                        singleLine = true
                    )
                }

                item {
                    OutlinedTextField(
                        value = storeName,
                        onValueChange = { storeName = it },
                        modifier = Modifier.fillMaxWidth().testTag("input_merchant_form_store"),
                        label = { Text("اسم المتجر أو النشاط *") },
                        singleLine = true
                    )
                }

                item {
                    OutlinedTextField(
                        value = location,
                        onValueChange = { location = it },
                        modifier = Modifier.fillMaxWidth().testTag("input_merchant_form_location"),
                        label = { Text("موقع المحل في أجدابيا *") },
                        singleLine = true
                    )
                }

                item {
                    OutlinedTextField(
                        value = discountType,
                        onValueChange = { discountType = it },
                        modifier = Modifier.fillMaxWidth().testTag("input_merchant_form_discount_type"),
                        label = { Text("نوع التخفيضات المقدمة *") },
                        singleLine = true
                    )
                }

                item {
                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        modifier = Modifier.fillMaxWidth().testTag("input_merchant_form_phone"),
                        label = { Text("رقم هاتف المتجر *") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        singleLine = true
                    )
                }

                item {
                    OutlinedTextField(
                        value = category,
                        onValueChange = { category = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("التصنيف (ملابس، صيدلية، مطعم...)") },
                        singleLine = true
                    )
                }

                item {
                    OutlinedTextField(
                        value = facebookUrl,
                        onValueChange = { facebookUrl = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("رابط صفحة فيسبوك (اختياري)") },
                        singleLine = true
                    )
                }

                if (errorMessage != null) {
                    item {
                        Text(
                            text = errorMessage ?: "",
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { submit() },
                modifier = Modifier.testTag("btn_confirm_save_merchant")
            ) {
                Text(if (isEditing) "حفظ التعديلات" else "حفظ وإنشاء الحساب")
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

@Composable
fun ActivationCodeItemCard(
    code: ActivationCodeEntity,
    onCopy: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("activation_code_item_${code.id}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        color = MaterialTheme.colorScheme.primaryContainer,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = code.code,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Surface(
                        color = if (code.isUsed) MaterialTheme.colorScheme.surfaceVariant else DinarGreen.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = if (code.isUsed) "مستخدم" else "جاهز للتفعيل 🟢",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (code.isUsed) MaterialTheme.colorScheme.onSurfaceVariant else DinarGreen,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "الباقة: ${code.planType}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Row {
                IconButton(
                    onClick = onCopy,
                    modifier = Modifier.testTag("btn_copy_code_${code.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = "نسخ الرمز",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.testTag("btn_delete_code_${code.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "حذف الرمز",
                        tint = FireRed
                    )
                }
            }
        }
    }
}
