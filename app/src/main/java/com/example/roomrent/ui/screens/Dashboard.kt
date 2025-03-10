import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.material3.CardDefaults.elevatedCardElevation
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.roomrent.data.RoomEntity
import com.example.roomrent.viewmodel.RoomViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun Dashboard(navController: NavController) {
    val context = LocalContext.current
    val viewModel: RoomViewModel = viewModel()
    val roomDataList by viewModel.roomData.collectAsStateWithLifecycle(emptyList())

    val roomNo = remember { mutableStateOf("") }
    val roomRent = remember { mutableStateOf("") }
    val electricityBill = remember { mutableStateOf("") }
    val amountPaid = remember { mutableStateOf("") }

    val totalAmount = remember { mutableStateOf(0) }
    val remainingBalance = remember { mutableStateOf(0) }

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
                        colors = listOf(Color(0x0271AAF1), Color(0x2059A9E8))
                    ),
                    shape = RoundedCornerShape(25.dp)
                )
                .padding(vertical = 24.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Room Rent Manager",
                fontWeight = FontWeight.Bold,
                color = Color.Black,
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
            elevation = elevatedCardElevation(8.dp)
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                InputField("Room No", roomNo.value) { input -> roomNo.value = input }
                InputField("Room Rent", roomRent.value) { input -> roomRent.value = input }
                InputField("Electricity Bill", electricityBill.value) { input -> electricityBill.value = input }
                InputField("Amount Paid", amountPaid.value) { input -> amountPaid.value = input }

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

                        val previousBalance = roomDataList
                            .filter { data -> data.roomNo == roomNo.value }
                            .maxByOrNull { data -> data.id ?: 0 }
                            ?.balance ?: 0

                        totalAmount.value = rent + bill + previousBalance
                        remainingBalance.value = totalAmount.value - paid

                        val entity = RoomEntity(
                            roomNo = roomNo.value,
                            month = getCurrentMonth(),
                            roomRent = rent,
                            electricityBill = bill,
                            total = totalAmount.value,
                            amountPaid = paid,
                            balance = remainingBalance.value
                        )

                        viewModel.insertRoomData(entity)

                        Toast.makeText(context, "Data saved", Toast.LENGTH_SHORT).show()

                        roomRent.value = ""
                        electricityBill.value = ""
                        amountPaid.value = ""
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors()
                ) {
                    Text(text = "Calculate & Save", fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = { showDialog.value = true },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors()
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
                    roomDataList.map { it.roomNo }
                        .distinct()
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
                                elevation = elevatedCardElevation(4.dp)
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
                val records = roomDataList.filter { it.roomNo == room }
                if (records.isEmpty()) {
                    Text("No records found.")
                } else {
                    Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                        records.forEach { record ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                shape = RoundedCornerShape(12.dp),
                                elevation = elevatedCardElevation(2.dp)
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
                                                viewModel.deleteRoomData(record)
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                                        ) {
                                            Text("Delete", color = Color.White)
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = {
                                records.forEach { viewModel.deleteRoomData(it) }
                                selectedRoom.value = ""
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Delete All Records", color = Color.White)
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

fun getCurrentMonth(): String {
    val months = listOf(
        "January", "February", "March", "April", "May", "June", "July", "August",
        "September", "October", "November", "December"
    )
    val now = java.util.Calendar.getInstance()
    return months[now.get(java.util.Calendar.MONTH)]
}
