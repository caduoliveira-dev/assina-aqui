"use client"

import {useId, useRef, useState} from "react"
import {CheckIcon, CopyIcon} from "lucide-react"

import {cn} from "@/lib/utils"
import {Input} from "@/components/ui/input"
import {Label} from "@/components/ui/label"
import {Textarea} from "@/components/ui/textarea"
import {
    Tooltip,
    TooltipContent,
    TooltipProvider,
    TooltipTrigger,
} from "@/components/ui/tooltip"

interface CopyIdSignerProps {
    signatureId: string
    signature?: string
}

export default function CopyIdSigner({signatureId, signature}: CopyIdSignerProps) {
    const idElementId = useId()
    const signatureElementId = useId()
    const [copiedId, setCopiedId] = useState<boolean>(false)
    const [copiedSignature, setCopiedSignature] = useState<boolean>(false)
    const idInputRef = useRef<HTMLInputElement>(null)
    const signatureInputRef = useRef<HTMLTextAreaElement>(null)

    const handleCopyId = () => {
        if (idInputRef.current) {
            navigator.clipboard.writeText(idInputRef.current.value)
            setCopiedId(true)
            setTimeout(() => setCopiedId(false), 1500)
        }
    }

    const handleCopySignature = () => {
        if (signatureInputRef.current) {
            navigator.clipboard.writeText(signatureInputRef.current.value)
            setCopiedSignature(true)
            setTimeout(() => setCopiedSignature(false), 1500)
        }
    }

    return (
        <div className="space-y-4">
            <div>
                <Label htmlFor={idElementId}>Signature ID - Copy to verify</Label>
                <div className="relative">
                    <Input
                        ref={idInputRef}
                        id={idElementId}
                        className="pe-9"
                        type="text"
                        value={signatureId}
                        readOnly
                    />
                    <TooltipProvider delayDuration={0}>
                        <Tooltip>
                            <TooltipTrigger asChild>
                                <button
                                    onClick={handleCopyId}
                                    className="text-muted-foreground/80 hover:text-foreground focus-visible:border-ring focus-visible:ring-ring/50 absolute inset-y-0 end-0 flex h-full w-9 items-center justify-center rounded-e-md transition-[color,box-shadow] outline-none focus:z-10 focus-visible:ring-[3px] disabled:pointer-events-none disabled:cursor-not-allowed"
                                    aria-label={copiedId ? "Copied" : "Copy to clipboard"}
                                    disabled={copiedId}
                                >
                                    <div
                                        className={cn(
                                            "transition-all",
                                            copiedId ? "scale-100 opacity-100" : "scale-0 opacity-0"
                                        )}
                                    >
                                        <CheckIcon
                                            className="stroke-emerald-500"
                                            size={16}
                                            aria-hidden="true"
                                        />
                                    </div>
                                    <div
                                        className={cn(
                                            "absolute transition-all",
                                            copiedId ? "scale-0 opacity-0" : "scale-100 opacity-100"
                                        )}
                                    >
                                        <CopyIcon size={16} aria-hidden="true"/>
                                    </div>
                                </button>
                            </TooltipTrigger>
                            <TooltipContent className="px-2 py-1 text-xs">
                                Copy to clipboard
                            </TooltipContent>
                        </Tooltip>
                    </TooltipProvider>
                </div>
            </div>


            {signature && (
                <div>
                    <Label htmlFor={signatureElementId}>Digital Signature</Label>
                    <div className="relative">
                        <Textarea
                            ref={signatureInputRef}
                            id={signatureElementId}
                            className="pe-9 resize-none"
                            value={signature}
                            readOnly
                            rows={4}
                        />
                        <TooltipProvider delayDuration={0}>
                            <Tooltip>
                                <TooltipTrigger asChild>
                                    <button
                                        onClick={handleCopySignature}
                                        className="text-muted-foreground/80 hover:text-foreground focus-visible:border-ring focus-visible:ring-ring/50 absolute top-2 end-2 flex h-8 w-8 items-center justify-center rounded transition-[color,box-shadow] outline-none focus:z-10 focus-visible:ring-[3px] disabled:pointer-events-none disabled:cursor-not-allowed"
                                        aria-label={copiedSignature ? "Copied" : "Copy to clipboard"}
                                        disabled={copiedSignature}
                                    >
                                        <div
                                            className={cn(
                                                "transition-all",
                                                copiedSignature ? "scale-100 opacity-100" : "scale-0 opacity-0"
                                            )}
                                        >
                                            <CheckIcon
                                                className="stroke-emerald-500"
                                                size={16}
                                                aria-hidden="true"
                                            />
                                        </div>
                                        <div
                                            className={cn(
                                                "absolute transition-all",
                                                copiedSignature ? "scale-0 opacity-0" : "scale-100 opacity-100"
                                            )}
                                        >
                                            <CopyIcon size={16} aria-hidden="true"/>
                                        </div>
                                    </button>
                                </TooltipTrigger>
                                <TooltipContent className="px-2 py-1 text-xs">
                                    Copy to clipboard
                                </TooltipContent>
                            </Tooltip>
                        </TooltipProvider>
                    </div>
                </div>
            )}
        </div>
    )
}
