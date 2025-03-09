// Your existing imports...
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun Dashboard(navController: NavController) {
    val context = LocalContext.current

    val roomNo = remember { mutableStateOf("") }
    val roomRent = remember { mutableStateOf("") }
    val electricityBill = remember { mutableStateOf("") }
    val amountPaid = remember { mutableStateOf("") }

    val totalAmount = remember { mutableStateOf(0) }
    val remainingBalance = remember { mutableStateOf(0) }

    val roomDataMap = remember { mutableStateMapOf<String, MutableList<RoomData>>() }

    val showDialog = remember { mutableStateOf(false) }
    val selectedRoom = remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFE3F2FD))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .border(2.dp, Color.DarkGray, RoundedCornerShape(25.dp))
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(Color(0xFF2196F3), Color(0xFF42A5F5))
                    ),
                    shape = RoundedCornerShape(25.dp)
                )
                .padding(vertical = 24.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Room Rent Manager",
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleLarge
            )
        }

        Spacer(modifier = Modifier.height(40.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(8.dp)
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                InputField("Room No", roomNo.value) { roomNo.value = it }
                InputField("Room Rent", roomRent.value) { roomRent.value = it }
                InputField("Electricity Bill", electricityBill.value) { electricityBill.value = it }
                InputField("Amount Paid", amountPaid.value) { amountPaid.value = it }

                Text(
                    text = "Total: ₹${totalAmount.value}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 8.dp)
                )
                Text(
                    text = "Remaining Balance: ₹${remainingBalance.value}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(top = 4.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        val rent = roomRent.value.toIntOrNull() ?: 0
                        val bill = electricityBill.value.toIntOrNull() ?: 0
                        val paid = amountPaid.value.toIntOrNull() ?: 0

                        val previousBalance = roomDataMap[roomNo.value]?.lastOrNull()?.balance ?: 0

                        totalAmount.value = rent + bill + previousBalance
                        remainingBalance.value = totalAmount.value - paid

                        val data = RoomData(
                            month = getCurrentMonth(),
                            roomRent = rent,
                            electricityBill = bill,
                            total = totalAmount.value,
                            amountPaid = paid,
                            balance = remainingBalance.value
                        )
                        roomDataMap.getOrPut(roomNo.value) { mutableListOf() }.add(data)

                        Toast.makeText(context, "Data saved", Toast.LENGTH_SHORT).show()

                        // Reset fields (except totalAmount and remainingBalance)
                        roomRent.value = ""
                        electricityBill.value = ""
                        amountPaid.value = ""
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(text = "Calculate & Save", fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = {
                        showDialog.value = true
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("View Previous Data")
                }
            }
        }
    }

    if (showDialog.value) {
        AlertDialog(
            onDismissRequest = { showDialog.value = false },
            title = { Text("Select Room No", fontWeight = FontWeight.Bold) },
            text = {
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    roomDataMap.keys
                        .sortedBy { it.toIntOrNull() ?: Int.MAX_VALUE }
                        .forEach { room ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .clickable {
                                        selectedRoom.value = room
                                        showDialog.value = false
                                    },
                                shape = RoundedCornerShape(12.dp),
                                elevation = CardDefaults.cardElevation(4.dp)
                            ) {
                                Box(modifier = Modifier.padding(16.dp)) {
                                    Text(text = "Room $room", fontWeight = FontWeight.Medium)
                                }
                            }
                        }
                }
            },
            confirmButton = {}
        )
    }

    selectedRoom.value.takeIf { it.isNotEmpty() }?.let { room ->
        AlertDialog(
            onDismissRequest = { selectedRoom.value = "" },
            title = { Text("Bills for Room $room", fontWeight = FontWeight.Bold) },
            text = {
                val records = roomDataMap[room]
                if (records.isNullOrEmpty()) {
                    Text("No records found.")
                } else {
                    Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                        records.forEachIndexed { index, record ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                shape = RoundedCornerShape(12.dp),
                                elevation = CardDefaults.cardElevation(2.dp)
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text("Month: ${record.month}")
                                    Text("Room Rent: ₹${record.roomRent}")
                                    Text("Electricity Bill: ₹${record.electricityBill}")
                                    Text("Total Amount: ₹${record.total}")
                                    Text("Amount Paid: ₹${record.amountPaid}")
                                    Text("Remaining Balance: ₹${record.balance}")
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Row(horizontalArrangement = Arrangement.End) {
                                        Button(
                                            onClick = {
                                                roomDataMap[room]?.removeAt(index)
                                                if (roomDataMap[room].isNullOrEmpty()) {
                                                    roomDataMap.remove(room)
                                                    selectedRoom.value = ""
                                                }
                                            },
                                            colors = ButtonDefaults.buttonColors(Color.Red)
                                        ) {
                                            Text("Delete")
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = {
                                roomDataMap.remove(room)
                                selectedRoom.value = ""
                            },
                            colors = ButtonDefaults.buttonColors(Color.Red),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Delete All Records")
                        }
                    }
                }
            },
            confirmButton = {}
        )
    }
}

@Composable
fun InputField(label: String, value: String, onValueChange: (String) -> Unit) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(text = label, style = MaterialTheme.typography.bodyMedium)
        Spacer(modifier = Modifier.height(4.dp))
        TextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text("Enter $label") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )
        Spacer(modifier = Modifier.height(12.dp))
    }
}

data class RoomData(
    val month: String,
    val roomRent: Int,
    val electricityBill: Int,
    val total: Int,
    val amountPaid: Int,
    val balance: Int
)

fun getCurrentMonth(): String {
    val months = listOf(
        "January", "February", "March", "April", "May", "June", "July", "August",
        "September", "October", "November", "December"
    )
    val now = java.util.Calendar.getInstance()
    return months[now.get(java.util.Calendar.MONTH)]
}
