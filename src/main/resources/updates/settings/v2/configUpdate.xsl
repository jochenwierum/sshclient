<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:strip-space elements="*" />
    <xsl:output omit-xml-declaration="no" indent="yes" />

    <xsl:template match="/">

        <settings version="2">
            <xsl:apply-templates />
        </settings>

    </xsl:template>

    <xsl:template match="settings">
        <xsl:apply-templates />
    </xsl:template>

    <xsl:template match="logtab">
        <logtab>
            <xsl:call-template name="tabstate" />
        </logtab>
    </xsl:template>

    <xsl:template match="keytab">
        <keytab>
            <xsl:call-template name="tabstate" />
        </keytab>
    </xsl:template>

    <xsl:template name="tabstate">
        <xsl:attribute name="state">
            <xsl:choose>
                <xsl:when test="text()='open'">OPEN</xsl:when>
                <xsl:when test="text()='always_open'">ALWAYS_OPEN</xsl:when>
                <xsl:when test="text()='always_closed'">ALWAYS_CLOSED</xsl:when>
                <xsl:otherwise>CLOSED</xsl:otherwise>
            </xsl:choose>
        </xsl:attribute>
    </xsl:template>

    <xsl:template match="language">
        <language>
            <xsl:value-of select="." />
        </language>
    </xsl:template>

    <xsl:template match="bellType">
        <xsl:choose>
            <xsl:when test="text()=0">
                <bellType>SOUND</bellType>
            </xsl:when>
            <xsl:when test="text()=2">
                <bellType>NONE</bellType>
            </xsl:when>
            <xsl:otherwise>
                <bellType>VISUAL</bellType>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

    <xsl:template match="passwords">
        <passwords check="{@check}">
            <passwords>
                <xsl:for-each select="password">
                    <xsl:copy-of select="." />
                </xsl:for-each>
            </passwords>
        </passwords>
    </xsl:template>

    <xsl:template match="keys">
        <keys>
            <xsl:for-each select="key">
                <item>
                    <xsl:value-of select="text()" />
                </item>
            </xsl:for-each>
        </keys>
    </xsl:template>

    <xsl:template match="defaultProfile">
        <defaultProfile>
            <xsl:call-template name="profile" />
        </defaultProfile>
    </xsl:template>

    <xsl:template match="profiles">
        <profiles>
            <xsl:for-each select="profile">
                <item id="{@name}">
                    <xsl:call-template name="profile" />
                </item>
            </xsl:for-each>
        </profiles>
    </xsl:template>

    <xsl:template name="profile">
        <host>
            <xsl:value-of select="host" />
        </host>
        <user>
            <xsl:value-of select="user" />
        </user>
        <port>
            <xsl:value-of select="port" />
        </port>
        <timeout>
            <xsl:value-of select="timeout" />
        </timeout>
        <keepAliveCount>
            <xsl:value-of select="keepAliveCount" />
        </keepAliveCount>
        <keepAliveInterval>
            <xsl:value-of select="keepAliveInterval" />
        </keepAliveInterval>
        <charset>
            <xsl:value-of select="charset" />
        </charset>
        <closeTabMode>
            <xsl:value-of select="closeTabMode" />
        </closeTabMode>
        <command>
            <xsl:value-of select="command" />
        </command>

        <forwardings>
            <xsl:apply-templates select="forwardings" />
        </forwardings>
        
        <gfxInfo>
            <xsl:apply-templates select="gfx" />
        </gfxInfo>
    </xsl:template>

    <xsl:template match="forwardings">
        <agent>
            <xsl:value-of select="forwardAgent" />
        </agent>
        <x11 enabled="{forwardX11/text()}">
            <host>
                <xsl:value-of select="x11Host" />
            </host>
            <display>
                <xsl:value-of select="x11Display" />
            </display>
            <xsl:if test="proxyPort/text()!=''">
                <proxyPort>
                    <xsl:value-of select="proxyPort" />
                </proxyPort>
            </xsl:if>
    
            <portforwardings>
                <xsl:for-each select="portForwardings/forwarding">
                    <xsl:call-template name="forwarding" />
                </xsl:for-each>
            </portforwardings>
        </x11>
    </xsl:template>
    
    <xsl:template name="forwarding">
        <item remoteHost="{@remoteHost}" remotePort="{@remotePort}"
            sourceHost="{@sourceHost}" sourcePort="{@sourcePort}">
            <xsl:attribute name="direction">
                <xsl:choose>
                <xsl:when test="@direction='remote'">REMOTE</xsl:when>
                <xsl:otherwise>LOCAL</xsl:otherwise>
                </xsl:choose>
            </xsl:attribute>
        </item>
    </xsl:template>
    
    <xsl:template match="gfx">
        <colors>
            <xsl:apply-templates select="colors" />
        </colors>
        <lightColors>
            <xsl:apply-templates select="lightColors" />
        </lightColors>
        <font name="{font/@name}" size="{font/@size}" />
        <cursor>
            <color><xsl:value-of select="cursorColor" /></color>
            <style blink="{cursorBlinks/text()}">
            <xsl:choose>
                <xsl:when test="cursorStyle/text()=1">UNDERLINE</xsl:when>
                <xsl:when test="cursorStyle/text()=2">HORIZONTAL</xsl:when>
                <xsl:otherwise>BLOCK</xsl:otherwise>
            </xsl:choose>
            </style>
        </cursor>
        <boundaryChars><xsl:value-of select="boundaryChars/text()" /></boundaryChars>
        <antiAliasingMode><xsl:value-of select="antiAliasing/@type" /></antiAliasingMode>
    </xsl:template>
    
    <xsl:template match="colors|lightColors">
        <xsl:for-each select="color">
            <item id="{@name}"><xsl:value-of select="@value" /></item>
        </xsl:for-each>
    </xsl:template>
</xsl:stylesheet>