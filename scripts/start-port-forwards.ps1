param(
    [string]$Namespace = "assignment-4"
)

$forwards = @(
    @{ Service = "product-service"; LocalPort = 18080; RemotePort = 8080 },
    @{ Service = "order-service"; LocalPort = 18081; RemotePort = 8081 },
    @{ Service = "prometheus"; LocalPort = 19090; RemotePort = 9090 },
    @{ Service = "grafana"; LocalPort = 13000; RemotePort = 3000 },
    @{ Service = "zipkin"; LocalPort = 19411; RemotePort = 9411 }
)

foreach ($forward in $forwards) {
    $service = $forward.Service
    $localPort = $forward.LocalPort
    $remotePort = $forward.RemotePort

    $title = "pf-$service-$localPort"
    $command = "`$Host.UI.RawUI.WindowTitle = '$title'; kubectl -n $Namespace port-forward svc/$service $localPort`:$remotePort"

    Start-Process -FilePath "pwsh" -ArgumentList @(
        "-NoExit",
        "-Command",
        $command
    )
}

Write-Host "Started $($forwards.Count) port-forward sessions in separate pwsh windows for namespace '$Namespace'."