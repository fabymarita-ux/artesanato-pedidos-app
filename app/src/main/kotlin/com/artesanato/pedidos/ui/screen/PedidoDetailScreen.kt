package com.artesanato.pedidos.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.artesanato.pedidos.ui.model.UiEtapa
import com.artesanato.pedidos.ui.theme.BackgroundColor
import com.artesanato.pedidos.ui.theme.Concluido
import com.artesanato.pedidos.ui.theme.PrimaryColor
import com.artesanato.pedidos.ui.theme.SecondaryColor
import com.artesanato.pedidos.ui.viewmodel.PedidoDetailViewModel
import java.time.format.DateTimeFormatter

@Composable
fun PedidoDetailScreen(
    viewModel: PedidoDetailViewModel,
    pedidoId: Int,
    onBackClick: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    var novaEtapaVisivel by remember { mutableStateOf(false) }
    var novaEtapaTitulo by remember { mutableStateOf("") }
    var novaEtapaDescricao by remember { mutableStateOf("") }

    LaunchedEffect(pedidoId) {
        viewModel.carregarDetalhes(pedidoId)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundColor)
    ) {
        if (state.isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = PrimaryColor
            )
        } else if (state.pedido != null) {
            val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
            val pedido = state.pedido!!

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                // Header
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            brush = androidx.compose.ui.graphics.Brush.linearGradient(
                                colors = listOf(PrimaryColor, SecondaryColor)
                            )
                        )
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = onBackClick) {
                            Icon(
                                Icons.Default.ArrowBack,
                                contentDescription = "Voltar",
                                tint = Color.White
                            )
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = pedido.cliente,
                                color = Color.White,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = pedido.categoria,
                                color = Color.White.copy(alpha = 0.8f),
                                fontSize = 14.sp
                            )
                        }
                    }
                }

                // Informações principais
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        InfoRow("Descrição:", pedido.descricao)
                        Spacer(modifier = Modifier.height(8.dp))
                        InfoRow(
                            "Entrega:",
                            pedido.dataEntrega.format(formatter),
                            PrimaryColor
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        InfoRow("Valor:", "R$ ${String.format("%.2f", pedido.valor)}", Color.Green)
                        Spacer(modifier = Modifier.height(8.dp))
                        InfoRow("Status:", pedido.status, SecondaryColor)
                    }
                }

                // Progresso
                if (state.etapas.isNotEmpty()) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp, 0.dp, 16.dp, 16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(4.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            val concluidas = state.etapas.count { it.concluida }
                            val total = state.etapas.size
                            val progresso = concluidas.toFloat() / total

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "Progresso das Etapas",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black
                                )
                                Text(
                                    text = "$concluidas/$total",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PrimaryColor
                                )
                            }
                            LinearProgressIndicator(
                                progress = { progresso },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(8.dp),
                                color = Concluido,
                                trackColor = Color.LightGray
                            )
                        }
                    }
                }

                // Etapas
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp, 0.dp, 16.dp, 16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(4.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Etapas do Pedido",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                            IconButton(
                                onClick = { novaEtapaVisivel = !novaEtapaVisivel },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    Icons.Default.Add,
                                    contentDescription = "Adicionar etapa",
                                    tint = PrimaryColor,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }

                        if (novaEtapaVisivel) {
                            Column(modifier = Modifier.padding(8.dp)) {
                                TextField(
                                    value = novaEtapaTitulo,
                                    onValueChange = { novaEtapaTitulo = it },
                                    placeholder = { Text("Título da etapa") },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(bottom = 8.dp)
                                )
                                TextField(
                                    value = novaEtapaDescricao,
                                    onValueChange = { novaEtapaDescricao = it },
                                    placeholder = { Text("Descrição (opcional)") },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(bottom = 8.dp),
                                    maxLines = 2
                                )
                                Button(
                                    onClick = {
                                        if (novaEtapaTitulo.isNotBlank()) {
                                            viewModel.adicionarEtapa(
                                                novaEtapaTitulo,
                                                novaEtapaDescricao,
                                                pedidoId,
                                                state.etapas.size
                                            )
                                            novaEtapaTitulo = ""
                                            novaEtapaDescricao = ""
                                            novaEtapaVisivel = false
                                        }
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryColor)
                                ) {
                                    Text("Adicionar", color = Color.White)
                                }
                            }
                        }

                        if (state.etapas.isEmpty()) {
                            Text(
                                text = "Nenhuma etapa adicionada",
                                fontSize = 12.sp,
                                color = Color.Gray,
                                modifier = Modifier.padding(8.dp)
                            )
                        } else {
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                state.etapas.forEach { etapa ->
                                    EtapaItem(
                                        etapa = etapa,
                                        onToggle = { viewModel.toggleEtapa(etapa) },
                                        onDelete = { viewModel.deletarEtapa(etapa) }
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun InfoRow(label: String, value: String, valueColor: Color = Color.Black) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color.Gray,
            modifier = Modifier.width(80.dp)
        )
        Text(
            text = value,
            fontSize = 12.sp,
            color = valueColor,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun EtapaItem(
    etapa: UiEtapa,
    onToggle: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White),
        colors = CardDefaults.cardColors(
            containerColor = if (etapa.concluida) Color(0xFFE8F5E9) else Color(0xFFFFF3E0)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = etapa.concluida,
                    onCheckedChange = { onToggle() },
                    modifier = Modifier.padding(end = 8.dp)
                )
                Column {
                    Text(
                        text = etapa.titulo,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (etapa.concluida) Color.Gray else Color.Black,
                        modifier = Modifier.padding(bottom = 2.dp)
                    )
                    if (etapa.descricao.isNotBlank()) {
                        Text(
                            text = etapa.descricao,
                            fontSize = 11.sp,
                            color = Color.Gray
                        )
                    }
                }
            }
            IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Deletar",
                    tint = Color.Red,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

fun Modifier.width(width: Dp): Modifier {
    return this.then(
        androidx.compose.foundation.layout.width(width)
    )
}