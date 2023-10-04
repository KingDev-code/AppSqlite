package com.example.sqlite

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.sqlite.ui.theme.SqliteTheme
import android.content.Context
import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.platform.LocalContext

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApp()
        }
    }
}

@Composable
fun MyApp() {
    SqliteTheme(darkTheme = true) { // Use darkTheme = true para o tema escuro
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("GFG")
                Spacer(modifier = Modifier.height(16.dp))
                CourseForm()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CourseForm() {
    var courseName by remember { mutableStateOf("") }
    var durationCourse by remember { mutableStateOf("") }
    var courseTracks by remember { mutableStateOf("") }
    var courseDescription by remember { mutableStateOf("") }

    // Declare a variável 'db' fora das funções lambdas
    val dbHelper = DBHelper(LocalContext.current)
    val db = dbHelper.writableDatabase // Use writableDatabase para o botão "Add Course"

    // Lista para armazenar os cursos lidos do banco de dados
    val cursos = remember { mutableStateListOf<String>() }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("SQLite Database in Android")

        OutlinedTextField(
            value = courseName,
            onValueChange = { courseName = it },
            label = { Text("Course Name") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = durationCourse,
            onValueChange = { durationCourse = it },
            label = { Text("Duration Course") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = courseTracks,
            onValueChange = { courseTracks = it },
            label = { Text("Course Tracks") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = courseDescription,
            onValueChange = { courseDescription = it },
            label = { Text("Course Description") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                // Lógica para adicionar o curso ao banco de dados
                val values = ContentValues()
                values.put("nome", courseName)
                values.put("duracao", durationCourse)
                values.put("tracks", courseTracks)
                values.put("descricao", courseDescription)

                val newRowId = db.insert("cursos", null, values)

                // Limpar os campos após a inserção
                courseName = ""
                durationCourse = ""
                courseTracks = ""
                courseDescription = ""
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            Text("Add Course to Database")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                // Lógica para ler os cursos do banco de dados
                val projection = arrayOf("nome", "duracao", "tracks", "descricao")
                val cursor: Cursor = db.query("cursos", projection, null, null, null, null, null)

                cursos.clear() // Limpar a lista antes de adicionar os cursos lidos

                while (cursor.moveToNext()) {
                    val nome = cursor.getString(cursor.getColumnIndexOrThrow("nome"))
                    val duracao = cursor.getString(cursor.getColumnIndexOrThrow("duracao"))
                    val tracks = cursor.getString(cursor.getColumnIndexOrThrow("tracks"))
                    val descricao = cursor.getString(cursor.getColumnIndexOrThrow("descricao"))
                    cursos.add("Nome: $nome, Duração: $duracao, Trilhas: $tracks, Descrição: $descricao")
                }

                cursor.close()
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
        ) {
            Text("Read Courses from Database")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Lista de cursos
        LazyColumn {
            items(cursos) { curso ->
                Text(curso)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CourseFormPreview() {
    MyApp()
}

class DBHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    companion object {
        private const val DATABASE_NAME = "Cursos.db"
        private const val DATABASE_VERSION = 1
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTableSQL = "CREATE TABLE IF NOT EXISTS cursos (id INTEGER PRIMARY KEY, nome TEXT, duracao TEXT, tracks TEXT, descricao TEXT)"
        db.execSQL(createTableSQL)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS cursos")
        onCreate(db)
    }
}