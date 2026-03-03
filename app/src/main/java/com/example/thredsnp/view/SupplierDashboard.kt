package com.example.thredsnp.view

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.thredsnp.ProductManager
import com.example.thredsnp.model.ProductItem
import com.example.thredsnp.view.ui.theme.THREDSNPTheme
import kotlinx.serialization.Serializable

@Serializable
data class Order(
    val id: String = "",
    val customer: String = "",
    val amount: String = "",
    val status: String = ""
)

class SupplierDashboard : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Ensure ProductManager is initialized
        ProductManager.init(this)
        
        enableEdgeToEdge()
        setContent {
            THREDSNPTheme {
                SupplierDashboardScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SupplierDashboardScreen() {
    var showAddProductDialog by remember { mutableStateOf(false) }
    var showMenu by remember { mutableStateOf(false) }
    var selectedTab by remember { mutableStateOf(0) }
    val context = LocalContext.current

    val products = ProductManager.products
    val orders = ProductManager.orders

    if (showAddProductDialog) {
        AddProductDialog(
            onDismiss = { showAddProductDialog = false },
            onAddProduct = { product, uri ->
                ProductManager.addProduct(context, product, uri)
                showAddProductDialog = false
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Supplier Portal", fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = { /* Open Notifications */ }) {
                        Icon(Icons.Default.Notifications, contentDescription = "Notifications")
                    }
                    Box {
                        IconButton(onClick = { showMenu = true }) {
                            Icon(Icons.Default.MoreVert, contentDescription = "More Options")
                        }
                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Logout") },
                                onClick = {
                                    context.startActivity(Intent(context, LoginScreen::class.java))
                                    (context as? ComponentActivity)?.finish()
                                }
                            )
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            if (selectedTab == 0) { // Show FAB only on Inventory tab
                FloatingActionButton(onClick = { showAddProductDialog = true }) {
                    Icon(Icons.Default.Add, contentDescription = "Add Product")
                }
            }
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Inventory, contentDescription = null) },
                    label = { Text("Inventory") },
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.AutoMirrored.Filled.ReceiptLong, contentDescription = null) },
                    label = { Text("Orders") },
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Analytics, contentDescription = null) },
                    label = { Text("Analytics") },
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 }
                )
            }
        }
    ) { innerPadding ->
        when (selectedTab) {
            0 -> InventoryScreen(innerPadding, products)
            1 -> OrdersScreen(innerPadding, orders)
            2 -> AnalyticsScreen(innerPadding)
        }
    }
}

@Composable
fun InventoryScreen(paddingValues: PaddingValues, products: List<ProductItem>) {
    val context = LocalContext.current
    var productToDelete by remember { mutableStateOf<ProductItem?>(null) }

    if (productToDelete != null) {
        AlertDialog(
            onDismissRequest = { productToDelete = null },
            title = { Text("Delete Product") },
            text = { Text("Are you sure you want to delete ${productToDelete?.name}?") },
            confirmButton = {
                Button(
                    onClick = {
                        productToDelete?.let { ProductManager.removeProduct(context, it) }
                        productToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { productToDelete = null }) {
                    Text("Cancel")
                }
            }
        )
    }

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier
            .padding(paddingValues)
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(products) { product ->
            ProductItemCard(
                product = product,
                onDelete = { productToDelete = product }
            )
        }
    }
}

@Composable
fun ProductItemCard(product: ProductItem, onDelete: () -> Unit) {
    Card(
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column {
            Box(
                modifier = Modifier
                    .height(120.dp)
                    .fillMaxWidth()
                    .background(Color.LightGray),
                contentAlignment = Alignment.Center
            ) {
                if (product.imageUrl.isNotBlank() && product.imageUrl != "null") {
                    AsyncImage(
                        model = product.imageUrl,
                        contentDescription = product.name,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(Icons.Default.HideImage, "no product image", modifier = Modifier.size(40.dp))
                }
                
                // Delete button in the top right corner of the image
                IconButton(
                    onClick = onDelete,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(4.dp)
                        .size(28.dp)
                        .background(Color.White.copy(alpha = 0.7f), RoundedCornerShape(14.dp))
                ) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = Color.Red,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
            Column(modifier = Modifier.padding(8.dp)) {
                Text(product.name, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(product.price, color = MaterialTheme.colorScheme.primary)
            }
        }
    }
}

@Composable
fun OrdersScreen(paddingValues: PaddingValues, orders: List<Order>) {
    Column(
        modifier = Modifier
            .padding(paddingValues)
            .fillMaxSize()
            .padding(16.dp)
    ) {
        val totalSales = orders.sumOf { it.amount.replace("NRP ", "").replace(",", "").toIntOrNull() ?: 0 }
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            StatCard("Total Sales", "NRP $totalSales", Icons.Default.ShoppingCart, Modifier.weight(1f))
            StatCard("Orders", "${orders.size}", Icons.AutoMirrored.Filled.List, Modifier.weight(1f))
        }
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "Customer Orders",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        if (orders.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No orders from customers yet")
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(orders) { order ->
                    OrderListItem(order)
                }
            }
        }
    }
}

@Composable
fun AnalyticsScreen(paddingValues: PaddingValues) {
    Box(
        modifier = Modifier
            .padding(paddingValues)
            .fillMaxSize(), contentAlignment = Alignment.Center
    ) {
        Text("Analytics Coming Soon!", fontSize = 20.sp)
    }
}

@Composable
fun AddProductDialog(onDismiss: () -> Unit, onAddProduct: (ProductItem, Uri?) -> Unit) {
    var productName by remember { mutableStateOf("") }
    var productPrice by remember { mutableStateOf("") }
    var productCategory by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    val launcher = rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri: Uri? ->
        imageUri = uri
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add New Product", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(8.dp))
                        .clickable { launcher.launch("image/*") },
                    contentAlignment = Alignment.Center
                ) {
                    if (imageUri != null) {
                        AsyncImage(
                            model = imageUri,
                            contentDescription = "Selected Image",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.AddAPhoto,
                                contentDescription = "Add Image",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "Upload Product Image",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = productName,
                    onValueChange = { productName = it },
                    label = { Text("Product Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = productPrice,
                    onValueChange = { productPrice = it },
                    label = { Text("Price (NRP)") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = productCategory,
                    onValueChange = { productCategory = it },
                    label = { Text("Category") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(onClick = {
                val newProduct = ProductItem(
                    id = "P${(0..1000).random()}",
                    name = productName,
                    price = "NRP $productPrice",
                    imageUrl = "" // Will be updated by ProductManager
                )
                onAddProduct(newProduct, imageUri)
            }) {
                Text("Add Product")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun StatCard(title: String, value: String, icon: ImageVector, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = title, fontSize = 14.sp, color = MaterialTheme.colorScheme.onPrimaryContainer)
            Text(text = value, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
        }
    }
}

@Composable
fun OrderListItem(order: Order) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(text = order.id, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(text = order.customer, fontSize = 14.sp, color = Color.Gray)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(text = order.amount, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                StatusChip(order.status)
            }
        }
    }
}

@Composable
fun StatusChip(status: String) {
    val color = when (status) {
        "Shipped" -> Color(0xFF4CAF50)
        "Delivered" -> Color(0xFF2196F3)
        else -> Color(0xFFFFA000)
    }
    Surface(
        color = color.copy(alpha = 0.1f),
        shape = RoundedCornerShape(16.dp)
    ) {
        Text(
            text = status,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = color
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SupplierDashboardPreview() {
    THREDSNPTheme {
        SupplierDashboardScreen()
    }
}
