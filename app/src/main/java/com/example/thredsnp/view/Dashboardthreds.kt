package com.example.thredsnp.view

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.thredsnp.ProductManager
import com.example.thredsnp.model.ProductItem
import com.example.thredsnp.view.ui.theme.THREDSNPTheme

data class Category(val name: String, val icon: ImageVector)
data class UserOrder(val id: String, val date: String, val amount: String, val items: List<ProductItem>)

class Dashboardthreds : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ProductManager.init(this)
        enableEdgeToEdge()
        setContent {
            THREDSNPTheme {
                DashboardScreen()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen() {
    val cartItems = remember { mutableStateListOf<ProductItem>() }
    val userOrders = remember { mutableStateListOf<UserOrder>() }
    var selectedTab by remember { mutableIntStateOf(0) }
    var showMenu by remember { mutableStateOf(false) }
    val context = LocalContext.current

    val categories = listOf(
        Category("Fashion", Icons.Default.Checkroom),
        Category("Sports", Icons.Default.SportsBasketball),
        Category("Shoes", Icons.Default.IceSkating)
    )

    val products = ProductManager.products
    val wishlist = ProductManager.wishlist

    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        text = "THREDS NP", 
                        fontWeight = FontWeight.ExtraBold,
                        color = Color(0xFF800000),
                        fontFamily = FontFamily.Serif,
                        fontStyle = FontStyle.Italic
                    ) 
                },
                actions = {
                    BadgedBox(
                        badge = {
                            if (cartItems.isNotEmpty()) {
                                Badge { Text(cartItems.size.toString()) }
                            }
                        },
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        IconButton(onClick = { 
                            if (cartItems.isNotEmpty()) {
                                val total = cartItems.sumOf { it.price.replace("NRP ", "").replace(",", "").toIntOrNull() ?: 0 }
                                val newOrder = Order(
                                    id = "#ORD${System.currentTimeMillis().toString().takeLast(4)}",
                                    customer = "Customer",
                                    amount = "NRP $total",
                                    status = "Pending"
                                )
                                ProductManager.placeOrder(context, newOrder)
                                val uiOrder = UserOrder(newOrder.id, "Today", newOrder.amount, cartItems.toList())
                                userOrders.add(0, uiOrder)
                                cartItems.clear()
                                Toast.makeText(context, "Order Placed Successfully!", Toast.LENGTH_SHORT).show()
                                selectedTab = 3 
                            } else {
                                Toast.makeText(context, "Cart is empty", Toast.LENGTH_SHORT).show()
                            }
                        }) {
                            Icon(Icons.Default.ShoppingCart, contentDescription = "Cart", tint = Color.Black)
                        }
                    }
                    Box {
                        IconButton(onClick = { showMenu = true }) {
                            Icon(Icons.Default.Person, contentDescription = "Profile", tint = Color.Black)
                        }
                        DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }) {
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
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Home, contentDescription = null) },
                    label = { Text("Home") },
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Search, contentDescription = null) },
                    label = { Text("Explore") },
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 }
                )
                NavigationBarItem(
                    icon = { Icon(if (selectedTab == 2) Icons.Default.Favorite else Icons.Default.FavoriteBorder, contentDescription = null) },
                    label = { Text("Wishlist") },
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 }
                )
                NavigationBarItem(
                    icon = { Icon(Icons.AutoMirrored.Filled.List, contentDescription = null) },
                    label = { Text("Orders") },
                    selected = selectedTab == 3,
                    onClick = { selectedTab = 3 }
                )
            }
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
            when (selectedTab) {
                0 -> HomeScreen(products, categories, wishlist, onAddToCart = { product ->
                    cartItems.add(product)
                    Toast.makeText(context, "${product.name} added to cart", Toast.LENGTH_SHORT).show()
                }, onToggleWishlist = { ProductManager.toggleWishlist(context, it) })
                2 -> WishlistScreen(wishlist, onAddToCart = { product ->
                    cartItems.add(product)
                    Toast.makeText(context, "${product.name} added to cart", Toast.LENGTH_SHORT).show()
                }, onRemove = { ProductManager.toggleWishlist(context, it) })
                3 -> OrdersScreen(userOrders)
                else -> Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Coming Soon")
                }
            }
        }
    }
}

@Composable
fun HomeScreen(products: List<ProductItem>, categories: List<Category>, wishlist: List<ProductItem>, onAddToCart: (ProductItem) -> Unit, onToggleWishlist: (ProductItem) -> Unit) {
    var searchQuery by remember { mutableStateOf("") }
    val filteredProducts = products.filter { it.name.contains(searchQuery, ignoreCase = true) }

    Column {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            placeholder = { Text("Search products...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            shape = RoundedCornerShape(12.dp),
            singleLine = true
        )
        Text(
            text = "Categories",
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF800000),
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
        LazyRow(contentPadding = PaddingValues(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            items(categories) { category -> CategoryItem(category) }
        }
        Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text(text = "Featured Products", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
            TextButton(onClick = { }) { Text("See All") }
        }
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(filteredProducts) { product ->
                ProductCard(product, isWishlisted = wishlist.contains(product), onAddToCart = { onAddToCart(product) }, onToggleWishlist = { onToggleWishlist(product) })
            }
        }
    }
}

@Composable
fun WishlistScreen(wishlist: List<ProductItem>, onAddToCart: (ProductItem) -> Unit, onRemove: (ProductItem) -> Unit) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(text = "My Wishlist", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color(0xFF800000), modifier = Modifier.padding(bottom = 16.dp))
        if (wishlist.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("Your wishlist is empty") }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(wishlist) { product ->
                    ProductCard(product, isWishlisted = true, onAddToCart = { onAddToCart(product) }, onToggleWishlist = { onRemove(product) })
                }
            }
        }
    }
}

@Composable
fun OrdersScreen(orders: List<UserOrder>) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(text = "My Orders", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color(0xFF800000), modifier = Modifier.padding(bottom = 16.dp))
        if (orders.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("No orders placed yet") }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(orders) { order -> OrderCard(order) }
            }
        }
    }
}

@Composable
fun OrderCard(order: UserOrder) {
    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(text = order.id, fontWeight = FontWeight.Bold)
                Text(text = order.date, color = Color.Gray)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "${order.items.size} Items", fontSize = 14.sp)
            Text(text = "Total: ${order.amount}", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Surface(color = Color(0xFF4CAF50).copy(alpha = 0.1f), shape = RoundedCornerShape(16.dp)) {
                Text(text = "Processing", modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), fontSize = 12.sp, fontWeight = FontWeight.Medium, color = Color(0xFF4CAF50))
            }
        }
    }
}

@Composable
fun CategoryItem(category: Category) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.clickable { }) {
        Box(modifier = Modifier.size(60.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primaryContainer), contentAlignment = Alignment.Center) {
            Icon(imageVector = category.icon, contentDescription = category.name, tint = MaterialTheme.colorScheme.onPrimaryContainer)
        }
        Text(text = category.name, fontSize = 12.sp, modifier = Modifier.padding(top = 4.dp))
    }
}

@Composable
fun ProductCard(product: ProductItem, isWishlisted: Boolean, onAddToCart: () -> Unit, onToggleWishlist: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)) {
        Column {
            Box(modifier = Modifier.fillMaxWidth().height(120.dp).background(Color.LightGray), contentAlignment = Alignment.Center) {
                if (product.imageUrl.isNotBlank() && product.imageUrl != "null") {
                    AsyncImage(model = product.imageUrl, contentDescription = product.name, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                } else {
                    Icon(Icons.Default.HideImage, contentDescription = null, modifier = Modifier.size(40.dp), tint = Color.Gray)
                }
                IconButton(onClick = onToggleWishlist, modifier = Modifier.align(Alignment.TopEnd).padding(4.dp).size(32.dp).background(Color.White.copy(alpha = 0.7f), CircleShape)) {
                    Icon(imageVector = if (isWishlisted) Icons.Default.Favorite else Icons.Default.FavoriteBorder, contentDescription = null, tint = if (isWishlisted) Color.Red else Color.Gray, modifier = Modifier.size(20.dp))
                }
            }
            Column(modifier = Modifier.padding(8.dp)) {
                Text(text = product.name, fontWeight = FontWeight.Medium, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(text = product.price, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Button(onClick = onAddToCart, modifier = Modifier.fillMaxWidth(), contentPadding = PaddingValues(vertical = 4.dp), shape = RoundedCornerShape(8.dp)) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Add to Cart", fontSize = 12.sp)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DashboardPreview() {
    THREDSNPTheme { DashboardScreen() }
}
