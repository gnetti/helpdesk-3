function Invoke-CommandSafely {
    param (
        [string]$Command,
        [string]$ErrorMessage
    )
    try {
        Invoke-Expression $Command
    } catch {
        Write-Host "Erro: $ErrorMessage" -ForegroundColor Red
        Write-Host $_.Exception.Message -ForegroundColor Red
        exit 1
    }
}

Write-Host "Deletando o package-lock.json..." -ForegroundColor Cyan
if (Test-Path "package-lock.json") {
    Remove-Item -Path "package-lock.json" -Force
    Write-Host "package-lock.json deletado com sucesso." -ForegroundColor Green
} else {
    Write-Host "package-lock.json nao encontrado." -ForegroundColor Yellow
}

Write-Host "Deletando a pasta node_modules..." -ForegroundColor Cyan
if (Test-Path "node_modules") {
    Invoke-CommandSafely "npx rimraf node_modules" "Falha ao deletar a pasta node_modules."
    Write-Host "Pasta node_modules deletada com sucesso." -ForegroundColor Green
} else {
    Write-Host "Pasta node_modules nao encontrada." -ForegroundColor Yellow
}

Write-Host "Instalando novamente as dependencias..." -ForegroundColor Cyan
Invoke-CommandSafely "npm install" "Falha ao instalar as dependencias."

Write-Host "Processo concluido com sucesso!" -ForegroundColor Green
