# Setup Instructions

## Database Configuration

The application requires MySQL to be running and configured. Follow these steps:

### 1. MySQL Configuration

The application connects to MySQL with these settings:
- **Host**: 127.0.0.1
- **Port**: 3306
- **User**: root
- **Database**: JavaControle (will be created automatically)

### 2. Set MySQL Password

You need to configure your MySQL root password in the file:
`src/main/resources/META-INF/persistence.xml`

Edit line 22 and replace the empty password with your MySQL root password:
```xml
<property name="hibernate.connection.password" value="YOUR_MYSQL_PASSWORD_HERE"/>
```

### 3. If You Don't Know Your MySQL Password

**Option A: Check MySQL Workbench**
- Open MySQL Workbench
- Look at your saved connections to see the password

**Option B: Reset MySQL Password**
1. Stop MySQL service: `net stop MySQL80`
2. Start MySQL in safe mode: `mysqld --skip-grant-tables`
3. Connect: `mysql -u root`
4. Reset password: `ALTER USER 'root'@'localhost' IDENTIFIED BY 'newpassword';`
5. Restart MySQL service normally

**Option C: Use MySQL Command Line**
- Try connecting: `mysql -u root -p`
- Enter your password when prompted
- If it works, use that password in persistence.xml

### 4. Run the Application

Once the password is configured, run:
```bash
run.bat
```

Or manually:
```bash
mvn clean compile
mvn exec:java -Dexec.mainClass="presentation.MainApp"
```

## Troubleshooting

### Error: "Connection refused"
- Make sure MySQL service is running: `Get-Service MySQL80`
- Check if MySQL is on port 3306: `netstat -an | findstr :3306`

### Error: "Access denied for user 'root'"
- Verify your MySQL password is correct in persistence.xml
- Test the connection manually: `mysql -u root -p`

### Error: "Maven not found"
- Install Maven and add it to your PATH
- Download from: https://maven.apache.org/download.cgi
