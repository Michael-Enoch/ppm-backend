param(
    [string]$MavenCommand = "mvn",
    [string]$LogPath = ".\mvn-run.log",
    [string]$ErrorPath = ".\mvn-run.err"
)

function Get-8080-Pids {
    netstat -aon | Select-String ":8080" | ForEach-Object {
        $columns = ($_ -split "\s+") | Where-Object { $_ -ne "" }
        if ($columns.Length -ge 5) {
            $columns[-1]
        }
    }
}

$pids = Get-8080-Pids
if ($pids) {
    $pids | ForEach-Object {
        Write-Host "Stopping process occupying port 8080: PID $_"
        taskkill /PID $_ /F | Out-Null
    }
} else {
    Write-Host "No process is listening on port 8080."
}

Write-Host "Verifying port 8080 is free..."
$remaining = Get-8080-Pids
if ($remaining) {
Write-Host "Port 8080 is still occupied by PID(s): $($remaining -join ', '), please rerun after those processes exit."
    exit 1
}

$arguments = "spring-boot:run"
$path = if ($MavenCommand -match "[\\/]" -or $MavenCommand.StartsWith(".\")) {
    (Resolve-Path $MavenCommand).ProviderPath
} else {
    $MavenCommand
}

$fullLogPath = [System.IO.Path]::GetFullPath($LogPath)
$fullErrPath = [System.IO.Path]::GetFullPath($ErrorPath)
$logDir = Split-Path $fullLogPath
$errDir = Split-Path $fullErrPath
if ($logDir) { New-Item -ItemType Directory -Force -Path $logDir | Out-Null }
if ($errDir) { New-Item -ItemType Directory -Force -Path $errDir | Out-Null }

Write-Host "Port 8080 is free. Launching `$path $arguments` in background (logs → $fullLogPath / $fullErrPath)."

$startInfo = @{
    FilePath               = $path
    ArgumentList           = $arguments
    WorkingDirectory       = (Get-Location).ProviderPath
    NoNewWindow            = $true
    RedirectStandardOutput = $fullLogPath
    RedirectStandardError  = $fullErrPath
    PassThru               = $true
}

$process = Start-Process @startInfo
Write-Host "Started Spring Boot process with PID $($process.Id). Tail the log with `Get-Content $LogPath -Wait`."
