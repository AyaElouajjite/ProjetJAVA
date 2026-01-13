# XAMPP MySQL Setup for Port 3307

## Current Status
- ✅ XAMPP MySQL is already configured for port 3307 in `c:\xampp\mysql\bin\my.ini`
- ✅ Your Java application is configured to connect to port 3307
- ⚠️ XAMPP Control Panel shows a port 3306 warning (this is a false alarm)

## Solution Steps

### Step 1: Disable MySQL80 Windows Service (if it exists)
The standalone MySQL80 service might conflict with XAMPP MySQL. To disable it:

1. Open Command Prompt as Administrator
2. Run: `sc config MySQL80 start= disabled`
3. Or use Services: Press `Win+R`, type `services.msc`, find MySQL80, right-click → Properties → Set Startup type to "Disabled"

### Step 2: Start XAMPP MySQL
1. Open XAMPP Control Panel
2. Click "Start" next to MySQL
3. Ignore the port 3306 warning (it's checking the default port, but MySQL is configured for 3307)
4. Check if MySQL status turns green

### Step 3: Verify MySQL is Running on Port 3307
Open Command Prompt and run:
```cmd
netstat -ano | findstr :3307
```

You should see MySQL listening on port 3307.

### Step 4: Test Connection
Test the connection with your password:
```cmd
mysql -u root -p -P 3307
```
Enter password: `WERT123@aya`

### Step 5: Run Your Application
Once MySQL is running on port 3307, run:
```cmd
cd "c:\Users\pc\Desktop\projetJava-master (3)\projetJava-master\gest_Rep"
run.bat
```

## Troubleshooting

### If MySQL Still Won't Start:

1. **Check the error log:**
   - Open: `c:\xampp\mysql\data\mysql_error.log`
   - Look for recent error messages

2. **Try starting MySQL manually:**
   ```cmd
   cd c:\xampp\mysql\bin
   mysqld --defaults-file=c:\xampp\mysql\bin\my.ini --standalone --console
   ```

3. **Check if port 3307 is free:**
   ```cmd
   netstat -ano | findstr :3307
   ```

4. **Verify my.ini configuration:**
   - Open `c:\xampp\mysql\bin\my.ini`
   - Ensure both `[client]` and `[mysqld]` sections have `port=3307`

## Alternative: Use MySQL80 Service Instead

If XAMPP MySQL continues to have issues, you can use the MySQL80 Windows service:

1. Configure MySQL80 to use port 3307 (edit `my.ini` in MySQL installation directory)
2. Start the service: `net start MySQL80` (as Administrator)
3. Your Java application will connect automatically
