# PowerShell script to stop the process listening on given TCP port (admin recommended)
param(
    [int]$Port = 8080
)
Write-Host "Looking for process listening on port $Port ..."
try {
    $proc = Get-NetTCPConnection -LocalPort $Port -State Listen -ErrorAction Stop | Select-Object -First 1
    if (-not $proc) {
        Write-Host "No process found listening on port $Port"
        exit 0
    }
    $pid = $proc.OwningProcess
    Write-Host "Found PID: $pid. Stopping process..."
    Stop-Process -Id $pid -Force -ErrorAction Stop
    Write-Host "Process $pid stopped. Port $Port freed."
} catch {
    Write-Host "Failed to free port $Port: $_"
    Write-Host "Try running PowerShell as Administrator."
    exit 1
}

