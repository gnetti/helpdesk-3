# Verifica se o Maven está instalado
$mvnPath = Get-Command mvn -ErrorAction SilentlyContinue

if (-not $mvnPath) {
    Write-Output "Maven não está instalado ou não está no PATH. Por favor, instale o Maven e adicione-o ao PATH."
    exit 1
}

# Define o caminho para o diretório .m2
$m2Path = "$env:USERPROFILE\.m2"

# Verifica se o diretório .m2 existe
if (Test-Path -Path $m2Path) {
    # Remove o diretório .m2 e seu conteúdo
    Remove-Item -Recurse -Force -Path $m2Path
    Write-Output "Diretório .m2 deletado em $m2Path"
} else {
    Write-Output "Diretório .m2 não existe em $m2Path"
}

# Define o caminho para o diretório do projeto Maven
# Altere isso para o caminho do seu projeto Maven
$mavenProjectPath = "X:\helpdesk-3\helpdesk-back"

# Muda para o diretório do projeto Maven
Set-Location -Path $mavenProjectPath

# Executa o comando Maven clean install
Write-Output "Executando 'mvn clean install' em $mavenProjectPath"
mvn clean install

# Verifica o código de saída do comando Maven
if ($LASTEXITCODE -eq 0) {
    Write-Output "Dependências do Maven reinstaladas com sucesso."
} else {
    Write-Output "Falha ao reinstalar as dependências do Maven."
}