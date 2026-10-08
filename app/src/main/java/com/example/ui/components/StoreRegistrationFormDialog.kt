package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.FireRed

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StoreRegistrationFormDialog(
    regularPrice: Double,
    hotPrice: Double,
    onDismiss: () -> Unit,
    onSubmit: (
        storeName: String,
        location: String,
        category: String,
        phone: String,
        whatsapp: String,
        facebookUrl: String,
        discountType: String,
        dealContent: String,
        isHot: Boolean
    ) -> Unit
) {
    // Form field states
    var storeName by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("مواد غذائية") }
    var selectedDiscountType by remember { mutableStateOf("عروض ساخنة وتخفيضات موسمية") }
    var phone by remember { mutableStateOf("") }
    var whatsapp by remember { mutableStateOf("") }
    var facebookUrl by remember { mutableStateOf("") }
    var dealContent by remember { mutableStateOf("") }
    var isHot by remember { mutableStateOf(false) }

    // Validation error states
    var storeNameError by remember { mutableStateOf<String?>(null) }
    var locationError by remember { mutableStateOf<String?>(null) }
    var phoneError by remember { mutableStateOf<String?>(null) }
    var facebookUrlError by remember { mutableStateOf<String?>(null) }
    var dealContentError by remember { mutableStateOf<String?>(null) }

    // Dropdown expanded states
    var categoryDropdownExpanded by remember { mutableStateOf(false) }
    var discountTypeDropdownExpanded by remember { mutableStateOf(false) }

    val categories = listOf(
        "مواد غذائية ومستلزمات",
        "ملابس وأحذية",
        "صيدليات وعناية صحية",
        "مطاعم وكافيهات",
        "إلكترونيات وهواتف",
        "عطور وهدايا ومستحضرات",
        "سيارات وقطع غيار",
        "مكتبات وقرطاسية",
        "أخرى"
    )

    val discountTypes = listOf(
        "عروض ساخنة وتخفيضات موسمية",
        "تخفيضات جملة وتجزئة",
        "خصم بنسبة مئوية (تصل إلى 50%)",
        "تصفية بضاعة ونهاية الموسم",
        "عروض يومية وأسبوعية خاصة"
    )

    // Form Validator Function
    fun validate(): Boolean {
        var isValid = true

        // Store Name Validation (minimum 3 characters)
        if (storeName.trim().isEmpty()) {
            storeNameError = "اسم المحل مطلوب"
            isValid = false
        } else if (storeName.trim().length < 3) {
            storeNameError = "يجب ألا يقل اسم المحل عن 3 أحرف"
            isValid = false
        } else {
            storeNameError = null
        }

        // Location Validation
        if (location.trim().isEmpty()) {
            locationError = "موقع المحل في أجدابيا مطلوب"
            isValid = false
        } else {
            locationError = null
        }

        // Libyan Phone Number Validation (e.g. 091, 092, 094, 218..., minimum 9 digits)
        val cleanPhone = phone.trim().replace(Regex("[^0-9+]"), "")
        if (cleanPhone.isEmpty()) {
            phoneError = "رقم هاتف المحل مطلوب"
            isValid = false
        } else if (cleanPhone.length < 9 || cleanPhone.length > 14) {
            phoneError = "يرجى إدخال رقم هاتف ليبي صحيح (9-10 أرقام)"
            isValid = false
        } else {
            phoneError = null
        }

        // Facebook URL Validation (if entered, must be valid)
        if (facebookUrl.trim().isNotEmpty()) {
            val url = facebookUrl.trim().lowercase()
            if (!url.startsWith("http://") && !url.startsWith("https://") && !url.contains("facebook.com") && !url.contains("fb.com")) {
                facebookUrlError = "يرجى إدخال رابط فيسبوك صحيح أو تركه فارغاً"
                isValid = false
            } else {
                facebookUrlError = null
            }
        } else {
            facebookUrlError = null
        }

        // Deal Content Validation
        if (dealContent.trim().isEmpty()) {
            dealContentError = "تفاصيل العرض والتخفيض مطلوبة"
            isValid = false
        } else if (dealContent.trim().length < 8) {
            dealContentError = "يجب ألا تقل تفاصيل العرض عن 8 أحرف"
            isValid = false
        } else {
            dealContentError = null
        }

        return isValid
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Storefront,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(26.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "تسجيل محل وإضافة تخفيضات",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        text = {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    Text(
                        text = "أدخل بيانات محلك وعرضك التخفيضي ليتم حفظها محلياً في قاعدة البيانات:",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                // 1. Store Name Field
                item {
                    OutlinedTextField(
                        value = storeName,
                        onValueChange = {
                            storeName = it
                            if (storeNameError != null) storeNameError = null
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("form_input_store_name"),
                        label = { Text("اسم المحل التجاري *") },
                        placeholder = { Text("مثال: أسواق السلام، بوتيك الأناقة...") },
                        leadingIcon = {
                            Icon(imageVector = Icons.Default.Storefront, contentDescription = null)
                        },
                        singleLine = true,
                        isError = storeNameError != null,
                        supportingText = {
                            storeNameError?.let {
                                Text(text = it, color = MaterialTheme.colorScheme.error, fontSize = 11.sp)
                            }
                        }
                    )
                }

                // 2. Store Location in Ajdabiya Field
                item {
                    OutlinedTextField(
                        value = location,
                        onValueChange = {
                            location = it
                            if (locationError != null) locationError = null
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("form_input_location"),
                        label = { Text("موقع ومقر المحل في أجدابيا *") },
                        placeholder = { Text("مثال: شارع إسطنبول، طريق بنغازي...") },
                        leadingIcon = {
                            Icon(imageVector = Icons.Default.LocationOn, contentDescription = null)
                        },
                        singleLine = true,
                        isError = locationError != null,
                        supportingText = {
                            locationError?.let {
                                Text(text = it, color = MaterialTheme.colorScheme.error, fontSize = 11.sp)
                            }
                        }
                    )
                }

                // 3. Category Dropdown
                item {
                    ExposedDropdownMenuBox(
                        expanded = categoryDropdownExpanded,
                        onExpandedChange = { categoryDropdownExpanded = !categoryDropdownExpanded }
                    ) {
                        OutlinedTextField(
                            value = selectedCategory,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("تصنيف النشاط التجاري") },
                            leadingIcon = {
                                Icon(imageVector = Icons.Default.Category, contentDescription = null)
                            },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryDropdownExpanded) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor()
                        )
                        ExposedDropdownMenu(
                            expanded = categoryDropdownExpanded,
                            onDismissRequest = { categoryDropdownExpanded = false }
                        ) {
                            categories.forEach { cat ->
                                DropdownMenuItem(
                                    text = { Text(cat) },
                                    onClick = {
                                        selectedCategory = cat
                                        categoryDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                // 4. Discount Type Dropdown
                item {
                    ExposedDropdownMenuBox(
                        expanded = discountTypeDropdownExpanded,
                        onExpandedChange = { discountTypeDropdownExpanded = !discountTypeDropdownExpanded }
                    ) {
                        OutlinedTextField(
                            value = selectedDiscountType,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("نوع التخفيضات المقدمة") },
                            leadingIcon = {
                                Icon(imageVector = Icons.Default.LocalOffer, contentDescription = null)
                            },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = discountTypeDropdownExpanded) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor()
                        )
                        ExposedDropdownMenu(
                            expanded = discountTypeDropdownExpanded,
                            onDismissRequest = { discountTypeDropdownExpanded = false }
                        ) {
                            discountTypes.forEach { dtype ->
                                DropdownMenuItem(
                                    text = { Text(dtype) },
                                    onClick = {
                                        selectedDiscountType = dtype
                                        discountTypeDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                // 5. Phone Number Field
                item {
                    OutlinedTextField(
                        value = phone,
                        onValueChange = {
                            phone = it
                            if (phoneError != null) phoneError = null
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("form_input_phone"),
                        label = { Text("رقم هاتف المحل للاتصال المباشر *") },
                        placeholder = { Text("مثال: 0925551234") },
                        leadingIcon = {
                            Icon(imageVector = Icons.Default.Phone, contentDescription = null)
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        singleLine = true,
                        isError = phoneError != null,
                        supportingText = {
                            phoneError?.let {
                                Text(text = it, color = MaterialTheme.colorScheme.error, fontSize = 11.sp)
                            }
                        }
                    )
                }

                // 6. WhatsApp Number (optional)
                item {
                    OutlinedTextField(
                        value = whatsapp,
                        onValueChange = { whatsapp = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("رقم الواتساب (اختياري، يترك فارغاً إذا نفس الهاتف)") },
                        placeholder = { Text("0912345678") },
                        leadingIcon = { Text("💬", modifier = Modifier.padding(start = 12.dp)) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        singleLine = true
                    )
                }

                // 7. Facebook Post/Page URL Field
                item {
                    OutlinedTextField(
                        value = facebookUrl,
                        onValueChange = {
                            facebookUrl = it
                            if (facebookUrlError != null) facebookUrlError = null
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("form_input_facebook_url"),
                        label = { Text("رابط منشور أو صفحة فيسبوك (اختياري)") },
                        placeholder = { Text("https://facebook.com/...") },
                        leadingIcon = {
                            Icon(imageVector = Icons.Default.Link, contentDescription = null)
                        },
                        singleLine = true,
                        isError = facebookUrlError != null,
                        supportingText = {
                            facebookUrlError?.let {
                                Text(text = it, color = MaterialTheme.colorScheme.error, fontSize = 11.sp)
                            }
                        }
                    )
                }

                // 8. Discount & Deal Content Field
                item {
                    OutlinedTextField(
                        value = dealContent,
                        onValueChange = {
                            dealContent = it
                            if (dealContentError != null) dealContentError = null
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("form_input_deal_content"),
                        label = { Text("تفاصيل العرض والتخفيض الحالي *") },
                        placeholder = { Text("مثال: خصم 30% على كافة الملابس الشتوية بمناسبة التصفية...") },
                        leadingIcon = {
                            Icon(imageVector = Icons.Default.Description, contentDescription = null)
                        },
                        minLines = 3,
                        maxLines = 5,
                        isError = dealContentError != null,
                        supportingText = {
                            dealContentError?.let {
                                Text(text = it, color = MaterialTheme.colorScheme.error, fontSize = 11.sp)
                            }
                        }
                    )
                }

                // 9. Ad Type Selection: Regular (3 LYD) vs Hot Deal (5 LYD)
                item {
                    Text(
                        text = "نوع الإعلان في التطبيق:",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Regular
                        Card(
                            onClick = { isHot = false },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (!isHot) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                                else MaterialTheme.colorScheme.surface
                            ),
                            border = BorderStroke(
                                1.dp,
                                if (!isHot) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                            )
                        ) {
                            Row(
                                modifier = Modifier.padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(selected = !isHot, onClick = { isHot = false })
                                Column {
                                    Text("عادي", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    Text("${regularPrice.toInt()} د.ل", fontSize = 11.sp, color = MaterialTheme.colorScheme.primary)
                                }
                            }
                        }

                        // Hot Deal
                        Card(
                            onClick = { isHot = true },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isHot) FireRed.copy(alpha = 0.12f)
                                else MaterialTheme.colorScheme.surface
                            ),
                            border = BorderStroke(
                                1.dp,
                                if (isHot) FireRed else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                            )
                        ) {
                            Row(
                                modifier = Modifier.padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(selected = isHot, onClick = { isHot = true })
                                Column {
                                    Text("ساخن 🔥", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = if (isHot) FireRed else MaterialTheme.colorScheme.onSurface)
                                    Text("${hotPrice.toInt()} د.ل", fontSize = 11.sp, color = FireRed)
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (validate()) {
                        onSubmit(
                            storeName.trim(),
                            location.trim(),
                            selectedCategory,
                            phone.trim(),
                            whatsapp.trim().ifEmpty { phone.trim() },
                            facebookUrl.trim(),
                            selectedDiscountType,
                            dealContent.trim(),
                            isHot
                        )
                    }
                },
                modifier = Modifier.testTag("btn_submit_store_form")
            ) {
                Text("حفظ في قاعدة البيانات", fontWeight = FontWeight.Bold)
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
