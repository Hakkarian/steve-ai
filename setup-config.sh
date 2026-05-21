#!/bin/bash

# Steve Environment Configuration Setup
# This script loads environment variables and creates the configuration file

echo "🔧 Setting up Steve configuration..."

# Create config directory if it doesn't exist
mkdir -p config

# Function to get environment variable or use default
get_env() {
    local var_name="$1"
    local default="$2"
    local value="${!var_name:-$default}"
    echo "$value"
}

# Load environment variables from .env file if it exists
if [ -f ".env" ]; then
    echo "📁 Loading environment variables from .env..."
    source .env
fi

# Get configuration values with defaults
AI_PROVIDER=$(get_env "STEVE_AI_PROVIDER" "openrouter")
OPENROUTER_API_KEY=$(get_env "OPENROUTER_API_KEY" "")
OPENAI_API_KEY=$(get_env "OPENAI_API_KEY" "")
OPENROUTER_MODEL=$(get_env "STEVE_OPENROUTER_MODEL" "openrouter/free")
OPENAI_MODEL=$(get_env "STEVE_OPENAI_MODEL" "gpt-4")
MAX_TOKENS=$(get_env "STEVE_MAX_TOKENS" "1000")
TEMPERATURE=$(get_env "STEVE_TEMPERATURE" "0.7")

# Create the configuration file
cat > config/steve-common.toml << EOF
[ai]
    # AI provider to use: 'groq' (FASTEST, FREE), 'openai', 'gemini', or 'openrouter'
    provider = "$AI_PROVIDER"

[openai]
    # Your OpenAI API key
    # Get your API key from: https://platform.openai.com/api-keys
    apiKey = "$OPENAI_API_KEY"
    
    # Using GPT-3.5-turbo (much cheaper than GPT-4 for testing)
    model = "$OPENAI_MODEL"

[openrouter]
    # Your OpenRouter API key
    # Get your OpenRouter API key from: https://openrouter.ai/keys
    apiKey = "$OPENROUTER_API_KEY"
    
    # OpenRouter model to use - using the free model
    model = "$OPENROUTER_MODEL"

[behavior]
    # Ticks between action checks (20 ticks = 1 second)
    actionTickDelay = 20
    
    # Allow Steves to respond in chat
    enableChatResponses = true
    
    # Maximum number of Steves that can be active simultaneously
    maxActiveSteves = 10
EOF

echo "✅ Configuration file created: config/steve-common.toml"
echo "🔑 Provider: $AI_PROVIDER"
echo "📊 Model: $OPENROUTER_MODEL (if using OpenRouter)"
echo "🚀 Ready to run: ./gradlew runClient"