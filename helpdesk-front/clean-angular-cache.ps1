# Função para executar comandos com tratamento de erro
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

# Deletar o package-lock.json
Write-Host "Deletando o package-lock.json..." -ForegroundColor Cyan
if (Test-Path "package-lock.json") {
    Remove-Item -Path "package-lock.json" -Force
    Write-Host "package-lock.json deletado com sucesso." -ForegroundColor Green
} else {
    Write-Host "package-lock.json n&atilde;o encontrado." -ForegroundColor Yellow
}

# Usar npx rimraf para deletar a pasta node_modules
Write-Host "Deletando a pasta node_modules..." -ForegroundColor Cyan
if (Test-Path "node_modules") {
    Invoke-CommandSafely "npx rimraf node_modules" "Falha ao deletar a pasta node_modules."
    Write-Host "Pasta node_modules deletada com sucesso." -ForegroundColor Green
} else {
    Write-Host "Pasta node_modules n&atilde;o encontrada." -ForegroundColor Yellow
}

# Reinstalar as dependências
Write-Host "Instalando novamente as depend&ecirc;ncias..." -ForegroundColor Cyan
Invoke-CommandSafely "npm install" "Falha ao instalar as depend&ecirc;ncias."

Write-Host "Processo conclu&iacute;do com sucesso!" -ForegroundColor Green