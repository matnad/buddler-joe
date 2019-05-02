#version 400 core

in vec2 pass_textureCoordinates;
in vec3 surfaceNormal;
in vec3 toLightVector[24];
in vec3 toCameraVector;
in float visibility;

out vec4 out_Colour;

uniform sampler2D backgroundTexture;
uniform sampler2D textureR;
uniform sampler2D textureG;
uniform sampler2D textureB;
uniform sampler2D blendMap;

uniform sampler2D modelTexture;
uniform vec3 lightColour[24];
uniform vec3 attenuation[24];
uniform vec3 lightDirection[24];
uniform float lightCutoff[24];
uniform float shineDamper;
uniform float reflectivity;
uniform vec3 skyColour;

void main(void) {

    vec4 blendMapColour = texture(blendMap, pass_textureCoordinates);

    float backTextureAmount = 1 - (blendMapColour.r + blendMapColour.g + blendMapColour.b);
    vec2 tiledCoords = pass_textureCoordinates * 8; // 8x8 terrains
    vec4 backgroundTextureColour = texture(backgroundTexture, tiledCoords) * backTextureAmount;
    vec4 rTextureColour = texture(textureR, tiledCoords) * blendMapColour.r;
    vec4 gTextureColour = texture(textureG, tiledCoords) * blendMapColour.g;
    vec4 bTextureColour = texture(textureB, tiledCoords) * blendMapColour.b;

    vec4 totalColour = backgroundTextureColour + rTextureColour + gTextureColour + bTextureColour;

    vec3 unitNormal = normalize(surfaceNormal);
    vec3 unitVectorToCamera = normalize(toCameraVector);

    vec3 totalDiffuse = vec3(0.0);
    vec3 totalSpecular = vec3(0.0);

    float lightMul = 100;

    for(int i=0; i<24; i++) {
        float damper = 6;
        if (i == 0) {
          damper = 2;
        }
        float theta = dot(normalize(toLightVector[i]), normalize(-lightDirection[i]));
        float epsilon = 0.15;
        float intensity = clamp((theta - lightCutoff[i]+epsilon) / epsilon, 0.0, 1.0);
        //if(theta > lightCutoff[i]) {
          float distance = length(toLightVector[i]);
          float attFactor = attenuation[i].x + (attenuation[i].y * distance) + (attenuation[i].z * distance * distance);
          vec3 unitLightVector = normalize(toLightVector[i]);
          float nDot1 = dot(unitNormal, unitLightVector);
          float brightness = max(nDot1, 0.0);
          vec3 lightDirection = -unitLightVector;
          vec3 reflectedLightDirection = reflect(lightDirection, unitNormal);
          float specularFactor = dot(reflectedLightDirection, unitVectorToCamera);
          specularFactor = max(specularFactor, 0.0);
          float dampedFactor = pow(specularFactor, shineDamper);
          totalDiffuse = totalDiffuse + (brightness * lightColour[i] / damper  * lightMul * intensity) / attFactor;
          totalSpecular = totalSpecular + (dampedFactor * reflectivity * lightColour[i] / 2 * lightMul * intensity) / attFactor;
        //}
    }
    totalDiffuse = max(totalDiffuse, 0.2);

    out_Colour = vec4(totalDiffuse, 1.0) * totalColour + vec4(totalSpecular, 1.0);
    out_Colour = mix(vec4(skyColour, 1.0), out_Colour, visibility / lightMul);

}