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

# Limpar o cache do Angular
Write-Host "Limpando o cache do Angular..." -ForegroundColor Cyan
Invoke-CommandSafely "ng cache clean" "Falha ao limpar o cache do Angular."

# Deletar o package-lock.json
Write-Host "Deletando o package-lock.json..." -ForegroundColor Cyan
if (Test-Path "package-lock.json") {
    Remove-Item -Path "package-lock.json" -Force
    Write-Host "package-lock.json deletado com sucesso." -ForegroundColor Green
} else {
    Write-Host "package-lock.json não encontrado." -ForegroundColor Yellow
}

# Usar npx rimraf para deletar a pasta node_modules
Write-Host "Deletando a pasta node_modules..." -ForegroundColor Cyan
if (Test-Path "node_modules") {
    Invoke-CommandSafely "npx rimraf node_modules" "Falha ao deletar a pasta node_modules."
    Write-Host "Pasta node_modules deletada com sucesso." -ForegroundColor Green
} else {
    Write-Host "Pasta node_modules não encontrada." -ForegroundColor Yellow
}

# Reinstalar as dependências
Write-Host "Instalando novamente as dependências..." -ForegroundColor Cyan
Invoke-CommandSafely "npm install" "Falha ao instalar as dependências."

Write-Host "Processo concluído com sucesso!" -ForegroundColor Green